package br.com.fiap.study_manager.dto;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.models.enums.Weekday;
import jakarta.validation.constraints.Min;

import java.time.LocalTime;

public record PlanItemRequest(

        StudyPlan studyPlan,
        Subject subject,
        String customTitle,
        Weekday weekday,
        LocalTime startTime,

        @Min(value = 1)
        Integer durationMinutes

) {
        public PlanItem toEntity() {
                return PlanItem.builder()
                        .studyPlan(studyPlan)
                        .subject(subject)
                        .customTitle(customTitle)
                        .weekday(weekday)
                        .startTime(startTime)
                        .durationMinutes(durationMinutes)
                        .build();
        }
}
