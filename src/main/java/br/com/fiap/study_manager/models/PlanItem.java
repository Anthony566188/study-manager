package br.com.fiap.study_manager.models;

import br.com.fiap.study_manager.converters.WeekdayConverter;
import br.com.fiap.study_manager.models.enums.Weekday;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "DB_PLAN_ITEMS")
public class PlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_STUDY_PLAN", nullable = false)
    private StudyPlan studyPlan;

    @ManyToOne
    @JoinColumn(name = "ID_SUBJECT") // Aqui pode ser null
    private Subject subject;

    @Column(name = "CUSTOM_TITLE")
    private String customTitle;

    @Column(name = "WEEKDAY")
    @Convert(converter = WeekdayConverter.class) // <--- GARANTE A CONVERSÃO
    private Weekday weekday;

    @Column(name = "START_TIME")
    private LocalTime startTime;

    @Column(name = "DURATION_MINUTES")
    private Integer durationMinutes;

    @Column(name = "DONE")
    private Boolean done = false;

}
