package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.StudyPlanType;
import br.com.fiap.study_manager.services.StudyPlanTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/study-plan-types")
public class StudyPlanTypeController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private StudyPlanTypeService service;

    @GetMapping
    public List<StudyPlanType> listAll(){
        log.info("Listando todos os tipos de planos de estudo...");
        return service.findAll();
    }

}
