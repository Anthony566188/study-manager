package br.com.fiap.study_manager.repository;

import br.com.fiap.study_manager.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource; // O Spring injeta a conexão do application.properties aqui

    // Registrando usuário
    public void registerUser(User user) {

        String sql = "INSERT INTO DB_USERS (username) VALUES (?)";

        String[] returnColumns = { "ID" };

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, returnColumns)) {

            ps.setString(1, user.getUsername());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }

            log.info("Usuario inserido com sucesso!");

        } catch (SQLException e) {
            log.error("Erro ao inserir Usuário: " + e.getMessage());
        }

    }

    // Listando usuários
    public List<User> listUsers(){

        List<User> users = new ArrayList<User>();

        //configurar a query
        String sql = "SELECT * FROM DB_USERS";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            //preparar o objeto para receber os dados do Oracle
            ResultSet rs = ps.executeQuery();

            // percorrer o ResultSet
            while(rs.next()) {
                long id = rs.getInt(1);
                String username = rs.getString(2);

                users.add(new User(id, username));
            }

        } catch (SQLException e) {
            log.error("Erro ao listar usuários!" + e.getMessage());
        }
        return users;
    }


}
