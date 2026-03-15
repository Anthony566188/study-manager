package br.com.fiap.study_manager.repository;

import br.com.fiap.study_manager.models.Subject;
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

@Repository
public class SubjectRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource;

    public void insertSubject(Subject subject) {

        User user = subject.getUser();

        String sql = "INSERT INTO DB_SUBJECTS (id_user, name) VALUES (?, ?)";

        String[] returnColumns = { "ID" };

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, returnColumns)) {

            ps.setLong(1, user.getId());
            ps.setString(2, subject.getName());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                subject.setId(rs.getLong(1));
            }

            log.info("Assunto inserido com sucesso!");

        } catch (SQLException e) {
            log.error("Erro ao inserir Assunto.", e);
            throw new RuntimeException("Erro ao inserir Assunto.", e);
        }

    }

}
