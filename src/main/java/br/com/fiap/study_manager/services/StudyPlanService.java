package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.BusinessException;
import br.com.fiap.study_manager.exceptions.ResourceNotFoundException;
import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.repository.StudyPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyPlanService {

    @Autowired
    StudyPlanRepository repository;

    public StudyPlan addStudyPlan(StudyPlan studyPlan) {
        if (studyPlan.getName() == null || studyPlan.getName().isBlank()) {
            throw new BusinessException("Nome do plano de estudo é obrigatório");
        }

        repository.insertStudyPlan(studyPlan);
        return studyPlan;

    }


    public List<StudyPlan> listUserStudyPlan(long id_user) {

        return repository.listUserStudyPlans(id_user);

    }

    public void deleteStudyPlan(long id) {

        int rows = repository.deleteStudyPlan(id);
        if (rows == 0) {
            throw new ResourceNotFoundException("Plano de estudo com id " + id + " não encontrado");
        }

    }
}
