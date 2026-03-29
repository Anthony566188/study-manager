package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.BusinessException;
import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.StudyBalance;
import br.com.fiap.study_manager.models.StudySession;
import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.repositories.StudyBalanceRepository;
import br.com.fiap.study_manager.repositories.StudySessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudySessionService {

    @Autowired
    private StudySessionRepository repository;

    @Autowired
    private StudyBalanceRepository balanceRepository;

    public StudySession addSession(StudySession studySession) {

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

                long minutosPlanejados = item
                        .getDurationMinutes() != null ? item.getDurationMinutes() : 0;
                long diferenca = minutosPlanejados - minutosEstudados;

                if (diferenca != 0) {
                    updateBalance(
                            existing.getUser()
                                    .getId(), item
                                    .getSubject().getId(), (int) diferenca
                    );
                }

            }

        } else if (existing.getSubject() != null) {
            /*
            * Estudo livre,
            * 100% do tempo estudado entra como abatimento da dívida
            * (valor negativo)
            */
            if (minutosEstudados > 0) {
                updateBalance(
                        existing.getUser()
                                .getId(), existing
                                .getSubject().getId(), (int) -minutosEstudados
                );
            }
        }

        return repository.save(existing);
    }

    // Método auxiliar para criar ou atualizar o saldo
    private void updateBalance(Long userId, Long subjectId, int minutosDiferenca) {

        StudyBalance balance = balanceRepository
                .findByUserIdAndSubjectId(userId, subjectId)
                .orElseGet(() -> {
                    // Se não existir saldo anterior, cria um registro zerado
                    StudyBalance novoSaldo = new StudyBalance();
                    novoSaldo.setUser(new br.com.fiap.study_manager.models.User(userId));
                    novoSaldo.setSubject(new br.com.fiap.study_manager.models.Subject());
                    novoSaldo.getSubject().setId(subjectId);
                    novoSaldo.setBalanceMinutes(0);
                    return novoSaldo;
                });

        // Soma ou subtrai os minutos (dependendo se a diferença for positiva ou negativa)
        balance.setBalanceMinutes(balance.getBalanceMinutes() + minutosDiferenca);
        balance.setUpdatedAt(LocalDateTime.now());

        balanceRepository.save(balance);
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
