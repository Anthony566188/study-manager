package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectService {

    @Autowired
    SubjectRepository repository;

    public Subject addSubject(Subject subject) {

        repository.insertSubject(subject);
        return subject;

    }
}
