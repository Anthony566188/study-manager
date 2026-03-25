package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.BusinessException;
import br.com.fiap.study_manager.exceptions.ResourceNotFoundException;
import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.repositories.PlanItemsRepository;
import br.com.fiap.study_manager.repositories.StudyPlanRepository;
import br.com.fiap.study_manager.repositories.StudySessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StudyPlanService {

    @Autowired
    StudyPlanRepository repository;

    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private PlanItemsRepository planItemsRepository;

    public StudyPlan addStudyPlan(StudyPlan studyPlan) {
        if (studyPlan.getName() == null || studyPlan.getName().isBlank()) {
            throw new BusinessException("Nome do plano de estudo é obrigatório");
        }

        return repository.save(studyPlan);

    }


    public List<StudyPlan> listUserStudyPlan(long idUser) {
        return repository.findByUserId(idUser);
    }

    @Transactional
    public void deleteStudyPlan(long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Plano de estudo com id " + id + " não encontrado");
        }

        // Deleta as sessões de estudo vinculadas aos itens deste plano
        studySessionRepository.deleteByStudyPlanId(id);

        // Deleta os itens de estudo deste plano
        planItemsRepository.deleteByStudyPlanId(id);

        // Deleta o plano
        repository.deleteById(id);

    }

    public StudyPlan updateStudyPlan(Long id, StudyPlan studyPlan) {

        if (studyPlan.getName() == null || studyPlan.getName().isBlank()) {
            throw new BusinessException("Nome do plano de estudo é obrigatório");
        }

        // Busca o plano existente no banco
        StudyPlan existingPlan = findStudyPlanById(id);

        // Atualiza apenas os campos permitidos
        existingPlan.setName(studyPlan.getName());
        existingPlan.setDescription(studyPlan.getDescription());

        // Salva a alteração
        return repository.save(existingPlan);
    }

    private StudyPlan findStudyPlanById(Long id){
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Plano de estudo de id " + id + " não encontrado"));
    }

}
