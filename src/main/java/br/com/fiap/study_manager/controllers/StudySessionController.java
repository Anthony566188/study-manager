package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.StudySession;
import br.com.fiap.study_manager.services.StudySessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timer/sessions")
public class StudySessionController {

    @Autowired
    private StudySessionService service;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @PostMapping
    public ResponseEntity<StudySession> createSession(@RequestBody StudySession studySession) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addSession(studySession));
    }

}
