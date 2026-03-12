package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.repository.StudyPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyPlanService {

    @Autowired
    StudyPlanRepository repository;

    public StudyPlan addStudyPlan(StudyPlan studyPlan) {
        repository.insertStudyPlan(studyPlan);
        return studyPlan;

    }
}
