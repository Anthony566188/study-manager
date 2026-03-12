package br.com.fiap.study_manager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyPlan {

    private long id;
    private StudyPlanType studyPlanType;
    private User user;
    private String name;
    private String description;

    // Construtor apenas com o id
    public StudyPlan(long id) {
        this.id = id;
    }

    // Construtor sem id
    public StudyPlan(StudyPlanType studyPlanType, User user, String name, String description) {
        this.studyPlanType = studyPlanType;
        this.user = user;
        this.name = name;
        this.description = description;
    }
}
