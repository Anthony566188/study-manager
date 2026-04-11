package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.BusinessException;
import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.StudySession;
import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.repositories.PlanItemsRepository;
import br.com.fiap.study_manager.repositories.StudySessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudySessionService {

    private final StudySessionRepository repository;

    private final PlanItemsRepository planItemsRepository;

    private final StudyBalanceService studyBalanceService;

    public StudySessionService(
            StudySessionRepository repository,
            PlanItemsRepository planItemsRepository,
            StudyBalanceService studyBalanceService
    ) {
        this.repository = repository;
        this.planItemsRepository = planItemsRepository;
        this.studyBalanceService = studyBalanceService;
    }

    public StudySession addSession(StudySession studySession) {

        // Verifica se a sessão está sendo atrelada a um Item da agenda
        if (studySession.getPlanItem() != null && studySession.getPlanItem().getId() != null) {

            // Busca o item completo
            PlanItem item = planItemsRepository.findById(studySession.getPlanItem().getId())
                    .orElseThrow(() -> new BusinessException("Item de plano não encontrado."));

            String tipoPlano = item.getStudyPlan().getStudyPlanType().getName();

            if ("Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Não é possível iniciar uma sessão para o tipo de plano 'Rotina Semanal'");
            }
        }

        // Pega o ID do usuário que está tentando iniciar a sessão
        Long userId = studySession.getUser().getId();

        // Verifica se já existe uma sessão rodando ou pausada para este usuário
        boolean hasActiveSession = repository.existsByUserIdAndStatusIn(
                userId,
                List.of("IN_PROGRESS", "PAUSED")
        );

        if (hasActiveSession) {
            throw new BusinessException(
                    "Você já possui uma sessão de estudo em andamento ou pausada. " +
                            "Encerre-a antes de iniciar outra."
            );
        }

        // Se passou na validação, inicia o novo cronômetro
        studySession.setStartedAt(LocalDateTime.now());
        studySession.setStatus("IN_PROGRESS"); // Define o status inicial
        studySession.setPausedAccumulatedSec(0); // Zera o cofrinho

        return repository.save(studySession);

    }

    // --- MÉTODO: PAUSAR ---
    public StudySession pauseSession(Long id) {

        StudySession existing = findStudySessionById(id);

        if (!"IN_PROGRESS".equals(existing.getStatus())) {
            throw new BusinessException
                    ("Apenas sessões em andamento podem ser pausadas.");
        }

        // Calcula os segundos desta "perna" de estudo
        long elapsedSeconds =
                Duration.between(existing.getStartedAt(), LocalDateTime.now())
                        .getSeconds();

        // Adiciona ao cofrinho
        int currentAccumulated =
                existing.getPausedAccumulatedSec()
                        != null ? existing.getPausedAccumulatedSec() : 0;

        existing.setPausedAccumulatedSec(currentAccumulated + (int) elapsedSeconds);

        existing.setStatus("PAUSED");

        return repository.save(existing);
    }

    // --- MÉTODO: RETOMAR (DESPAUSAR) ---
    public StudySession resumeSession(Long id) {

        StudySession existing = findStudySessionById(id);

        if (!"PAUSED".equals(existing.getStatus())) {
            throw new BusinessException
                    ("Apenas sessões pausadas podem ser retomadas.");
        }

        // Reinicia o relógio para a nova perna de estudo
        existing.setStartedAt(LocalDateTime.now());
        existing.setStatus("IN_PROGRESS");

        return repository.save(existing);
    }

    public StudySession stopStudySession(Long id) {

        // Pega o objeto completo com base no id
        StudySession existing = findStudySessionById(id);

        // Verifica se a sessão já foi encerrada
        if ("COMPLETED".equals(existing.getStatus())) {
            throw new BusinessException("Esta sessão já foi encerrada.");
        }

        // Pega o horário do 'stop'
        LocalDateTime now = LocalDateTime.now();
        existing.setEndedAt(now);

        // Resgata o que já estava acumulado nas pausas
        long totalSeconds =
                existing.getPausedAccumulatedSec()
                        != null ? existing.getPausedAccumulatedSec() : 0;

        /*
            Se o usuário clicou em 'stop' enquanto o cronômetro estava rodando,
            soma a última perna
        */
        if ("IN_PROGRESS".equals(existing.getStatus())) {
            totalSeconds += Duration
                    .between(existing.getStartedAt(), now)
                    .getSeconds();
        }

        // Atualiza o status
        existing.setStatus("COMPLETED");

        // Converte os segundos totais em minutos reais de estudo
        long minutosEstudados = totalSeconds / 60;

        // VERIFICA O TIPO DE SESSÃO
        if (existing.getPlanItem() != null) {

            PlanItem item = existing.getPlanItem();
            String tipoPlano = item.getStudyPlan().getStudyPlanType().getName();

            if ("Híbrido".equalsIgnoreCase(tipoPlano) && item.getSubject() != null) {

                if (minutosEstudados > 0) {
                    studyBalanceService.updateBalance(
                            existing.getUser().getId(),
                            item.getSubject().getId(),
                            (int) -minutosEstudados // Valor negativo para esvaziar o pote
                    );
                }

            }

            if ("Ciclo".equalsIgnoreCase(tipoPlano)) {

                int currentCompleted = item.getCompletedMinutes() != null ? item.getCompletedMinutes() : 0;

                // Soma os minutos estudados na sessão de agora com o que já estava feito
                item.setCompletedMinutes(currentCompleted + (int) minutosEstudados);

                // Salva a atualização no banco
                planItemsRepository.save(item);
            }

        }

        /*
         *  Essa condição é ativada quando o endpoint de compensação é chamado
         */
        if (existing.getPlanItem() == null && existing.getSubject() != null) {
            /*
            * Estudo livre,
            * 100% do tempo estudado entra como abatimento da dívida
            * (valor negativo)
            */
            if (minutosEstudados > 0) {
                studyBalanceService.updateBalance(
                        existing.getUser()
                                .getId(), existing
                                .getSubject().getId(), (int) -minutosEstudados
                );
            }
        }

        return repository.save(existing);
    }

    // --- MÉTODO: INICIAR SESSÃO DE COMPENSAÇÃO ---
    public StudySession addCompensatorySession(Long userId, Long subjectId) {

        boolean hasActiveSession = repository
                .existsByUserIdAndStatusIn(userId, List.of("IN_PROGRESS", "PAUSED"));

        if (hasActiveSession) {
            throw new BusinessException("Você já possui uma sessão em andamento.");
        }

        StudySession session = new StudySession();
        session.setUser(new br.com.fiap.study_manager.models.User(userId));

        Subject subject = new Subject();
        subject.setId(subjectId);
        session.setSubject(subject);

        session.setStartedAt(LocalDateTime.now());
        session.setStatus("IN_PROGRESS");
        session.setPausedAccumulatedSec(0);

        return repository.save(session);
    }

    private StudySession findStudySessionById(Long id){
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus
                        .NOT_FOUND, "Sessão de id " + id + " não encontrado")
        );
    }

}
