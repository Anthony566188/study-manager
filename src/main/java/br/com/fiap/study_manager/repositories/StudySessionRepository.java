package br.com.fiap.study_manager.repositories;

import br.com.fiap.study_manager.models.StudySession;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    // Deleta todas as sessões associadas a um PlanItem específico
    @Transactional
    void deleteByPlanItemId(Long planItemId);

}
