package br.com.fiap.study_manager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyPlanType {

    private long id;
    private String name;
    private String description;

    public StudyPlanType(long id) {
        this.id = id;
    }

    public StudyPlanType(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
