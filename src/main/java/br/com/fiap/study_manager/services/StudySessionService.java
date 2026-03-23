package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.StudyBalance;
import br.com.fiap.study_manager.models.StudySession;
import br.com.fiap.study_manager.repositories.StudyBalanceRepository;
import br.com.fiap.study_manager.repositories.StudySessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class StudySessionService {

    @Autowired
    private StudySessionRepository repository;

    @Autowired
    private StudyBalanceRepository balanceRepository;

    public StudySession addSession(StudySession studySession) {

        studySession.setStartedAt(LocalDateTime.now());

        return repository.save(studySession);

    }

    public StudySession stopStudySession(Long id) {

        // Pega o objeto completo com base no id
        StudySession existing = findItemById(id);

        // Passa o horário do 'stop'
        existing.setEndedAt(LocalDateTime.now());

        // Atualiza o status
        existing.setStatus("COMPLETED");

        // Calcula o tempo real que o usuário ficou estudando
        long minutosEstudados = Duration.between(existing.getStartedAt(), existing.getEndedAt()).toMinutes();

        PlanItem item = existing.getPlanItem();
        String tipoPlano = item.getStudyPlan().getStudyPlanType().getName();

        // A REGRA DE NEGÓCIO: Aplica saldo apenas se for "Híbrido" E se for um "Subject" (não custom title)
        if ("Híbrido".equalsIgnoreCase(tipoPlano) && item.getSubject() != null) {

            // Pega quanto tempo ele deveria ter estudado
            long minutosPlanejados = item.getDurationMinutes() != null ? item.getDurationMinutes() : 0;

            // Exemplo: Planejou 120min, estudou 90min. Diferença = +30min (Está devendo 30min)
            // Exemplo 2: Planejou 60min, estudou 80min. Diferença = -20min (Tem 20min de crédito)
            long diferenca = minutosPlanejados - minutosEstudados;

            if (diferenca != 0) {
                atualizarSaldo(existing.getUser().getId(), item.getSubject().getId(), (int) diferenca);
            }
        }

        return repository.save(existing);

    }


    // Método auxiliar para criar ou atualizar o saldo
    private void atualizarSaldo(Long userId, Long subjectId, int minutosDiferenca) {

        StudyBalance balance = balanceRepository.findByUserIdAndSubjectId(userId, subjectId)
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

    private StudySession findItemById(Long id){
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão de id " + id + " não encontrado")
        );
    }

}
