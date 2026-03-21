package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.repositories.PlanItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        PlanItem existing = findItemById(id);

        existing.setSubject(planItem.getSubject());
        existing.setCustomTitle(planItem.getCustomTitle());
        existing.setWeekday(planItem.getWeekday());
        existing.setStartTime(planItem.getStartTime());
        existing.setDurationMinutes(planItem.getDurationMinutes());

        return repository.save(existing);
    }

    public void deletePlanItem(Long id) {

        repository.deleteById(id);

    }

    private PlanItem findItemById(Long id){
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de id " + id + " não encontrado")
        );
    }

}
