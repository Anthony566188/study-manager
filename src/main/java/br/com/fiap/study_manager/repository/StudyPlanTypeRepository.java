package br.com.fiap.study_manager.repository;

import br.com.fiap.study_manager.models.StudyPlanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class StudyPlanTypeRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource; // O Spring injeta a conexão do application.properties aqui

    // Insere um tipo de plano de estudo
    public void insertStudyPlanType(StudyPlanType studyPlanType) {

        String sql = "INSERT INTO DB_STUDY_PLANS_TYPES (name, description) " +
                "VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studyPlanType.getName());
            ps.setString(2, studyPlanType.getDescription());

            ps.executeUpdate();

            log.info("Estratégia de estudo inserida com sucesso!");

        } catch (SQLException e) {
            log.error("Erro ao inserir Estratégia de estudo: " + e.getMessage());
        }

    }

}
