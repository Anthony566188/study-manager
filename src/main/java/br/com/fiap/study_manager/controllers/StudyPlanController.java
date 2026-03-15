package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.services.StudyPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/study-plans")
public class StudyPlanController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    StudyPlanService service;

    @PostMapping
    public ResponseEntity<StudyPlan> createStudyPlan(@RequestBody StudyPlan studyPlan){ //binding
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addStudyPlan(studyPlan));
    }

    @GetMapping("/{idUser}")
    public List<StudyPlan> listUserStudyPlan(@PathVariable long idUser){
        log.info("Listando os planos de estudo do usuário de id {}...", idUser);
        return service.listUserStudyPlan(idUser);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudyPlan(@PathVariable long id){
        log.info("Deletando plano de estudo com id {}...", id );
        service.deleteStudyPlan(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<StudyPlan> updateStudyPlan(@PathVariable Long id, @RequestBody StudyPlan studyPlan){
        log.info("Atualizando plano de estudo com id {} com os dados {}", id, studyPlan);
        return ResponseEntity.ok( service.updateStudyPlan(id, studyPlan) );
    }

}
