package br.com.fiap.study_manager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DB_STUDY_PLANS")
public class StudyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_STUDY_PLAN_TYPE", nullable = false)
    private StudyPlanType studyPlanType;

    @ManyToOne
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
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
