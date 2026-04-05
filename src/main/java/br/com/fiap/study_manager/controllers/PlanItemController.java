package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.dto.PlanItemRequest;
import br.com.fiap.study_manager.dto.PlanItemResponse;
import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.enums.Weekday;
import br.com.fiap.study_manager.services.PlanItemService;
import jakarta.validation.Valid;
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
    public ResponseEntity<PlanItem> createPlanItem(@RequestBody @Valid PlanItemRequest planItemRequest){ //binding
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addPlanItem(planItemRequest.toEntity()));
    }

    @GetMapping("/{idStudyPlan}")
    public List<PlanItemResponse> listItems(@PathVariable long idStudyPlan){
        log.info("Listando os itens do plano de estudo de id {}...", idStudyPlan);
        return service.listItems(idStudyPlan).stream()
                .map(PlanItemResponse::fromEntity)
                .toList();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePlanItem(@PathVariable Long id) {
        log.info("Deletando item de id {}...", id );
        service.deletePlanItem(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<PlanItem>
    updatePlanItem(@PathVariable Long id, @RequestBody PlanItem planItem){
        log.info("Atualizando item com id {} com os dados {}", id, planItem);
        return ResponseEntity.ok( service.updatePlanItem(id, planItem) );
    }

    // Endpoint para marcar e desmarcar item
    @PatchMapping("{id}/done")
    public ResponseEntity<PlanItem> togglePlanItemDone(@PathVariable Long id) {
        log.info("Alterando status de conclusão do item de id {}...", id);
        return ResponseEntity.ok(service.toggleDone(id));
    }

    // Endpoint para restar todas as tarefas de um dia
    @PatchMapping("/plans/{idStudyPlan}/reset/{weekday}")
    public ResponseEntity<Void> resetDoneByWeekday(
            @PathVariable Long idStudyPlan,
            @PathVariable Weekday weekday) {

        log.info("Desmarcando todas as tarefas do plano {} para o dia {}", idStudyPlan, weekday);
        service.resetDoneByWeekday(idStudyPlan, weekday);

        return ResponseEntity.noContent().build(); // Retorna 204 No Content (sucesso sem corpo de resposta)
    }

    @PatchMapping("/plans/{idStudyPlan}/reset")
    public ResponseEntity<Void> resetCycleByStudyPlan(
            @PathVariable Long idStudyPlan) {

        log.info("Resetando os minutos completos do plano de id {} do tipo 'Ciclo'.", idStudyPlan);
        service.resetCycle(idStudyPlan);

        return ResponseEntity.noContent().build();
    }

}
