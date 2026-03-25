package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.BusinessException;
import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.repositories.PlanItemsRepository;
import br.com.fiap.study_manager.repositories.StudyBalanceRepository;
import br.com.fiap.study_manager.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class SubjectService {

    @Autowired
    SubjectRepository repository;

    @Autowired
    private StudyBalanceRepository balanceRepository;

    @Autowired
    private PlanItemsRepository planItemsRepository;

    public Subject addSubject(Subject subject) {

        if (subject.getName() == null || subject.getName().isBlank()) {
            throw new BusinessException("Nome da matéria é obrigatório");
        }

        return repository.save(subject);

    }

    public List<Subject> getAllUserSubjects(Long idUser) {

        return repository.findByUserId(idUser);

    }

    // Para deletar uma Subject, precisamos remover primeiro dependências
    // (DB_STUDY_BALANCE -> Subject e DB_PLAN_ITEMS -> Subject).
    @Transactional
    public void deleteSubject(Long id) {

        // 1) Apaga o "banco de horas" da subject
        balanceRepository.deleteBySubject_Id(id);

        // 2) Desvincula a subject dos itens de plano (mantém o item do plano)
        planItemsRepository.nullifySubjectForSubjectId(id);

        // 3) Por fim, apaga a própria subject
        repository.deleteById(id);

    }

    public Subject updateSubject(Long id, Subject newSubject) {
        //BeanUtils.copyProperties(newSubject, subject, "id");
        newSubject.setId(id);
        return repository.save(newSubject);
    }

}
