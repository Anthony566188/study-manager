package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.models.StudyPlanType;
import br.com.fiap.study_manager.services.StudyPlanService;
import br.com.fiap.study_manager.services.StudyPlanTypeService;
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

    @GetMapping("/{id_user}")
    public List<StudyPlan> listUserStudyPlan(@PathVariable long id_user){
        log.info("Listando os planos de estudo do usuário de id {}...", id_user);
        return service.listUserStudyPlan(id_user);
    }



}
