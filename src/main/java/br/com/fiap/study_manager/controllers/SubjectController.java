package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.services.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    SubjectService service;

    @PostMapping
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject){ //binding
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addSubject(subject));
    }

    @GetMapping("{idUser}")
    public List<Subject> listAll(@PathVariable Long idUser) {
        log.info("Listando os 'subjects' do usuário de id {}...", idUser);
        return service.getAllUserSubjects(idUser);
    }

}
