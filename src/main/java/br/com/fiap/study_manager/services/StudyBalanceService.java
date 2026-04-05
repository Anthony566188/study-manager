package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.StudyBalance;
import br.com.fiap.study_manager.repositories.StudyBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudyBalanceService {

    @Autowired
    private StudyBalanceRepository repository;

    public List<StudyBalance> getAllUserBalances(Long idUser) {
        return repository.findByUserId(idUser);
    }

    // Método para criar ou atualizar o saldo
    public void updateBalance(Long userId, Long subjectId, int minutosDiferenca) {

        StudyBalance balance = repository
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

        repository.save(balance);
    }


}
