package br.com.fiap.study_manager.services;

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
        repository.insertStudyPlan(studyPlan);
        return studyPlan;

    }


    public List<StudyPlan> listUserStudyPlan(long id_user) {

        return repository.listUserStudyPlans(id_user);

    }

    public void deleteStudyPlan(long id) {

        repository.deleteStudyPlan(id);

    }
}
