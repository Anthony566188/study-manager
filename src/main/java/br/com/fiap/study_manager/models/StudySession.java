package br.com.fiap.study_manager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicInsert
@Table(name = "DB_STUDY_SESSIONS")
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PLAN_ITEM", nullable = false)
    private PlanItem planItem;

    @ManyToOne
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "STARTED_AT")
    private LocalDateTime startedAt;

    @Column(name = "ENDED_AT")
    private LocalDateTime endedAt;

    @Column(name = "PAUSED_ACCUMULATED_SEC")
    private Integer pausedAccumulatedSec;

}
