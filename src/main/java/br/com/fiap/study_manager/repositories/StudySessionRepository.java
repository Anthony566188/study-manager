package br.com.fiap.study_manager.repositories;

import br.com.fiap.study_manager.models.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
}
