package br.com.fiap.study_manager.repositories;

import br.com.fiap.study_manager.models.StudySession;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    // Deleta todas as sessões associadas a um PlanItem específico
    @Transactional
    void deleteByPlanItemId(Long planItemId);

    // Deleta todas as sessões baseadas no id do plano de estudo
    @Modifying
    @Transactional
    @Query("DELETE FROM StudySession s WHERE s.planItem.studyPlan.id = :studyPlanId")
    void deleteByStudyPlanId(Long studyPlanId);

    // --- Verifica se o usuário tem sessões com os status informados ---
    boolean existsByUserIdAndStatusIn(Long userId, List<String> statuses);

    // Deleta todas as sessões associadas DIRETAMENTE a uma matéria (Sessões de Compensação)
    @Modifying
    @Transactional
    @Query("DELETE FROM StudySession s WHERE s.subject.id = :subjectId")
    void deleteBySubjectId(Long subjectId);

}
