package br.com.fiap.study_manager.repositories;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.enums.Weekday;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
