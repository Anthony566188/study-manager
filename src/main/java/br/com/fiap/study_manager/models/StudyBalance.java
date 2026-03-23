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
@Table(name = "DB_STUDY_BALANCE")
public class StudyBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ID_SUBJECT", nullable = false)
    private Subject subject;

    @Column(name = "BALANCE_MINUTES")
    private Integer balanceMinutes;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}
