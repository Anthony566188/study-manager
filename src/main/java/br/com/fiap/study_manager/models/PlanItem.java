package br.com.fiap.study_manager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanItem {

    private Long id;
    private StudyPlan studyPlan;
    private String weekday;
    private LocalTime startTime;
    private Integer durationMinutes;

}
