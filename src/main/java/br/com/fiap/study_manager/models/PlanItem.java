package br.com.fiap.study_manager.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class PlanItem {

    private long id;
    private StudyPlan studyPlan;
    private String weekday;
    private LocalTime startTime;
    private LocalTime durationMinutes;

}
