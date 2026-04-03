package br.com.fiap.study_manager.dto;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.models.enums.Weekday;

import java.time.LocalTime;

public record PlanItemResponse(
    Long id,
    StudyPlan studyPlan,
    Subject subject,
    String customTitle,
    Weekday weekday,
    LocalTime startTime,
    Integer durationMinutes,
    Boolean done,
    Integer completedMinutes,
    Integer remainingMinutes // Campo para enviar o tempo restante
) {
    public static PlanItemResponse fromEntity(PlanItem planItem) {

        // Cálculo do tempo restante isolado no DTO
        Integer remaining = null;

        String tipoPlano = planItem.getStudyPlan().getStudyPlanType().getName();

        if ("Ciclo".equalsIgnoreCase(tipoPlano)) {
            int completed = (planItem.getCompletedMinutes() != null) ? planItem.getCompletedMinutes() : 0;
            remaining = Math.max(0, planItem.getDurationMinutes() - completed);
        }

        return new PlanItemResponse(
                planItem.getId(),
                planItem.getStudyPlan(),
                planItem.getSubject(),
                planItem.getCustomTitle(),
                planItem.getWeekday(),
                planItem.getStartTime(),
                planItem.getDurationMinutes(),
                planItem.getDone(),
                planItem.getCompletedMinutes(),
                remaining
        );
    }
}
