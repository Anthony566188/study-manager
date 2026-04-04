package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.BusinessException;
import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.repositories.PlanItemsRepository;
import br.com.fiap.study_manager.repositories.StudyBalanceRepository;
import br.com.fiap.study_manager.repositories.SubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(getClass());

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
    @Transactional
    public void deleteSubject(Long id) {

        // Apaga o "banco de horas" da subject
        balanceRepository.deleteBySubject_Id(id);

        // Desvincula a subject dos itens de plano (mantém o item do plano)
        int itensAfetados = planItemsRepository.nullifySubjectForSubjectId(id);
        log.info("O 'Subject' foi desvinculado de {} itens.", itensAfetados);

        // Apaga o subject
        repository.deleteById(id);

    }

    public Subject updateSubject(Long id, Subject newSubject) {
        //BeanUtils.copyProperties(newSubject, subject, "id");
        newSubject.setId(id);
        return repository.save(newSubject);
    }

}
