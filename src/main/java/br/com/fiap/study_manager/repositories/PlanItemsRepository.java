package br.com.fiap.study_manager.repositories;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.enums.Weekday;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface PlanItemsRepository extends JpaRepository<PlanItem, Long> {

    // O Spring Data JPA entende o nome do método e gera o SELECT automaticamente
    List<PlanItem> findByStudyPlanId(Long studyPlanId);

    // Busca itens por plano e dia, ordenados do mais cedo para o mais tarde
    List<PlanItem> findByStudyPlanIdAndWeekdayOrderByStartTimeAsc(Long studyPlanId, Weekday weekday);

    // Verifica se já existe um item no mesmo plano, dia e horário
    boolean existsByStudyPlanIdAndWeekdayAndStartTime(Long studyPlanId, Weekday weekday, LocalTime startTime);

    // Ao deletar uma Subject, precisamos garantir que não existe FK ativa para ela em DB_PLAN_ITEMS.
    @Modifying
    @Query("update PlanItem p set p.subject = null where p.subject.id = :subjectId")
    int nullifySubjectForSubjectId(Long subjectId);

    // Deleta todos os itens baseados no id do plano de estudo
    @Modifying
    @Transactional
    @Query("DELETE FROM PlanItem p WHERE p.studyPlan.id = :studyPlanId")
    void deleteByStudyPlanId(Long studyPlanId);

    // Reseta o status 'done' para false de todos os itens de um plano em um dia específico
    @Modifying
    @Transactional
    @Query("UPDATE PlanItem p SET p.done = false WHERE p.studyPlan.id = :studyPlanId AND p.weekday = :weekday")
    void resetDoneByStudyPlanAndWeekday(Long studyPlanId, Weekday weekday);

    // Reseta o 'completedMinutes' de todos os itens de um plano
    @Modifying
    @Transactional
    @Query("UPDATE PlanItem p SET p.completedMinutes = 0 WHERE p.studyPlan.id = :studyPlanId")
    void resetCycleByStudyPlan(Long studyPlanId);

}
