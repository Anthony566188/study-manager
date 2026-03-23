package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.StudyBalance;
import br.com.fiap.study_manager.repositories.StudyBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyBalanceService {

    @Autowired
    private StudyBalanceRepository repository;

    public List<StudyBalance> getAllUserBalances(Long idUser) {
        return repository.findByUserId(idUser);
    }


}
