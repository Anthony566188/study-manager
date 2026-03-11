package br.com.fiap.study_manager.repository;

import br.com.fiap.study_manager.models.User;
import br.com.fiap.study_manager.models.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserAuthRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PasswordEncoder passwordEncoder; // Codificador

    @Autowired
    private DataSource dataSource; // O Spring injeta a conexão do application.properties aqui

    // Cadastrar o login do usuário
    public void registerAuth(UserAuth userAuth) {

        // Obter o objeto User a partir do UserAuth
        User user = userAuth.getUser();

        String sql = "INSERT INTO DB_USER_AUTH (id_user, login, password) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Gera o hash da senha pura vinda do objeto UserAuth
            String encodedPassword = passwordEncoder.encode(userAuth.getPassword());

            ps.setLong(1, user.getId());
            ps.setString(2, userAuth.getLogin());
            ps.setString(3, encodedPassword);

            ps.executeUpdate();
            log.info("Login do Usuário criado com sucesso!");

        } catch (Exception e) {
            log.error("Erro ao inserir o Login do Usuário: " + e.getMessage());
        }

    }

    // Verifica o login no banco de dados
    public long verifyCredentials(String login, String password) {

        String sql = "SELECT id_user, password FROM DB_USER_AUTH WHERE login = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("PASSWORD");

                    // Compara a senha digitada com o hash do banco
                    if (passwordEncoder.matches(password, hashedPassword)) {
                        log.info("Usuário logado com sucesso!");
                        return rs.getLong("id_user");
                    }
                }

            }


        } catch (SQLException e) {
            log.error("Erro ao verificar credenciais: ", e);
        }
        return -1;
    }

    public UserAuth findByLogin(String login) {
        String sql = "SELECT login, password FROM DB_USER_AUTH WHERE login = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Aqui você pode retornar um novo objeto UserAuth com os dados do banco
                    return new UserAuth(null, rs.getString("login"), rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            log.error("Erro ao buscar login para Spring Security: " + e.getMessage());
        }
        return null;
    }

}
