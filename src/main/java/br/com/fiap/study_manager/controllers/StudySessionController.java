package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.StudySession;
import br.com.fiap.study_manager.services.StudySessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // --- ROTA: PAUSAR ---
    @PatchMapping("/{id}/pause")
    public ResponseEntity<StudySession> pauseStudySession(@PathVariable Long id){
        log.info("Pausando sessão com id {}", id);
        return ResponseEntity.ok( service.pauseSession(id) );
    }

    // --- ROTA: RETOMAR ---
    @PatchMapping("/{id}/resume")
    public ResponseEntity<StudySession> resumeStudySession(@PathVariable Long id){
        log.info("Retomando sessão com id {}", id);
        return ResponseEntity.ok( service.resumeSession(id) );
    }

    @PatchMapping("/{id}/stop")
    public ResponseEntity<StudySession>
    stopStudySession(@PathVariable Long id){
        log.info("Encerrando sessão com id {}", id);
        return ResponseEntity.ok( service.stopStudySession(id) );
    }

    @PostMapping("/compensate/users/{userId}/subjects/{subjectId}")
    public ResponseEntity<StudySession> createCompensatorySession(
            @PathVariable Long userId,
            @PathVariable Long subjectId) {

        log.info("Iniciando sessão de compensação para o usuário {} na matéria {}"
                , userId, subjectId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addCompensatorySession(userId, subjectId));
    }

}
