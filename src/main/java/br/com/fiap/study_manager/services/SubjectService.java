package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.BusinessException;
import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.repositories.PlanItemsRepository;
import br.com.fiap.study_manager.repositories.StudyBalanceRepository;
import br.com.fiap.study_manager.repositories.StudySessionRepository;
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

    private final StudySessionRepository studySessionRepository;

    private final PlanItemService planItemService;

    public SubjectService(
            StudySessionRepository studySessionRepository
            , PlanItemService planItemService
    ) {
        this.studySessionRepository = studySessionRepository;
        this.planItemService = planItemService;
    }

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

        // Apaga as sessões de compensação vinculadas diretamente a esta matéria
        studySessionRepository.deleteBySubjectId(id);

        // Busca todos os itens que usam esta matéria
        List<PlanItem> itensDaMateria = planItemsRepository.findBySubjectId(id);

        // Usa o método do PlanItemService para deletar cada item com segurança!
        for (PlanItem item : itensDaMateria) {
            /* * Ao chamar esse método, ele automaticamente:
             * - Deleta as StudySessions do item
             * - Deleta o Item
             * - RECALCULA as horas dos itens vizinhos no dia!
             */
            planItemService.deletePlanItem(item.getId());
        }

        // Apaga o subject
        repository.deleteById(id);

    }

    public Subject updateSubject(Long id, Subject newSubject) {
        //BeanUtils.copyProperties(newSubject, subject, "id");
        newSubject.setId(id);
        return repository.save(newSubject);
    }

}
