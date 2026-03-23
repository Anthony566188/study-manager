package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.StudySession;
import br.com.fiap.study_manager.repositories.StudySessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StudySessionService {

    @Autowired
    private StudySessionRepository repository;

    public StudySession addSession(StudySession studySession) {

        studySession.setStartedAt(LocalDateTime.now());

        return repository.save(studySession);

    }
}
