package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.services.PlanItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plan-items")
public class PlanItemController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    PlanItemService service;

    @PostMapping
    public ResponseEntity<PlanItem> createPlanItem(@RequestBody PlanItem planItem){ //binding
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addPlanItem(planItem));
    }

    @GetMapping("/{idStudyPlan}")
    public List<PlanItem> listItems(@PathVariable long idStudyPlan){
        log.info("Listando os itens do plano de estudo de id {}...", idStudyPlan);
        return service.listItems(idStudyPlan);
    }

    @PutMapping("{id}")
    public ResponseEntity<PlanItem>
    updatePlanItem(@PathVariable Long id, @RequestBody PlanItem planItem){
        log.info("Atualizando item com id {} com os dados {}", id, planItem);
        return ResponseEntity.ok( service.updatePlanItem(id, planItem) );
    }

}
