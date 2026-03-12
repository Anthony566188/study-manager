package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.repository.PlanItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanItemService {

    @Autowired
    PlanItemsRepository repository;

    public PlanItem addPlanItem(PlanItem planItem) {

        repository.insertPlanItem(planItem);
        return planItem;

    }
}
