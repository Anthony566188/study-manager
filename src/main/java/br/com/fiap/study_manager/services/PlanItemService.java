package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.ResourceNotFoundException;
import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.repositories.PlanItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanItemService {

    @Autowired
    PlanItemsRepository repository;

    public PlanItem addPlanItem(PlanItem planItem) {
        return repository.save(planItem);
    }

    public List<PlanItem> listItems(long idStudyPlan) {

        return repository.findByStudyPlanId(idStudyPlan);

    }

    public PlanItem updatePlanItem(Long id, PlanItem planItem) {
        PlanItem existing = repository.findById(id)
                .orElseThrow(() -> 
                new ResourceNotFoundException("PlanItem não encontrado com id: " + id));

        existing.setSubject(planItem.getSubject());
        existing.setCustomTitle(planItem.getCustomTitle());
        existing.setWeekday(planItem.getWeekday());
        existing.setStartTime(planItem.getStartTime());
        existing.setDurationMinutes(planItem.getDurationMinutes());

        return repository.save(existing);
    }
}
