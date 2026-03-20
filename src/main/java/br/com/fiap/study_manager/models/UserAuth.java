package br.com.fiap.study_manager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DB_USER_AUTH")
public class UserAuth {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Mapeia o id do usuário para o id do UserAuth
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @Column(name = "LOGIN", nullable = false, unique = true)
    private String login;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

}
