package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.BusinessException;
import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    SubjectRepository repository;

    public Subject addSubject(Subject subject) {

        if (subject.getName() == null || subject.getName().isBlank()) {
            throw new BusinessException("Nome da matéria é obrigatório");
        }

        return repository.save(subject);

    }

    public List<Subject> getAllUserSubjects(Long idUser) {

        return repository.findByUserId(idUser);

    }

    public void deleteSubject(Long id) {

        repository.deleteById(id);

    }
}
