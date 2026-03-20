package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.StudyPlanType;
import br.com.fiap.study_manager.repositories.StudyPlanTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyPlanTypeService {

    @Autowired
    StudyPlanTypeRepository repository;

    public List<StudyPlanType> findAll(){
        return repository.listAll();
    }

}
