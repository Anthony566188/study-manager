package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    SubjectService service;

    @PostMapping
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject){ //binding
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addSubject(subject));
    }

}
