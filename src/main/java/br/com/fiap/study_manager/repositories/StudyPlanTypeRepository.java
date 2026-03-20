package br.com.fiap.study_manager.repositories;

import br.com.fiap.study_manager.models.StudyPlanType;
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
            log.error("Erro ao inserir Estratégia de estudo.", e);
            throw new RuntimeException("Erro ao inserir Estratégia de estudo.", e);
        }

    }

    // Listar todos os tipos de plano de estudo
    public List<StudyPlanType> listAll() {

        // Cria a lista que vai armazenar os tipos de plano de estudo
        List<StudyPlanType> studyPlanTypes = new ArrayList<>();

        String sql = "SELECT * FROM DB_STUDY_PLAN_TYPES";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ResultSet rs = ps.executeQuery();

            /* Pegando os valores dos atributos do banco
            e passando para os atributos do java */
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");

                studyPlanTypes.add(
                        new StudyPlanType(id, name, description));
            }

        } catch (SQLException e) {
            log.error("Erro ao listar tipos de plano de estudo: ", e);
            throw new RuntimeException("Erro ao listar tipos de plano de estudo.", e);
        }
        return studyPlanTypes;
    }

}
