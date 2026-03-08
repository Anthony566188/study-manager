package br.com.fiap.study_manager.repository;

import br.com.fiap.study_manager.models.User;
import br.com.fiap.study_manager.models.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Repository
public class UserAuthRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource; // O Spring injeta a conexão do application.properties aqui

    public void registerAuth(UserAuth userAuth) {

        // Obter o objeto User a partir do UserAuth
        User user = userAuth.getUser();

        String sql = "INSERT INTO DB_USER_AUTH (id_user, login, password) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, user.getId());
            ps.setString(2, userAuth.getLogin());
            ps.setString(3, userAuth.getPassword());

            ps.executeUpdate();
            log.info("Login do Usuário criado com sucesso!");

        } catch (Exception e) {
            log.error("Erro ao inserir o Login do Usuário: " + e.getMessage());
        }

    }

}
