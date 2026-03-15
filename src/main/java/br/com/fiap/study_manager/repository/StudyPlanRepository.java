package br.com.fiap.study_manager.repository;

import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.models.StudyPlanType;
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
public class StudyPlanRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource; // O Spring injeta a conexão do application.properties aqui

    // Cria um plano de estudo
    public void insertStudyPlan(StudyPlan studyPlan) {

        // Obter o objeto User a partir do StudyPlan
        StudyPlanType studyPlanType = studyPlan.getStudyPlanType();
        User user = studyPlan.getUser();

        String sql = "INSERT INTO DB_STUDY_PLANS " +
                "(id_study_plan_type, id_user, name, description) " +
                "VALUES (?, ?, ?, ?)";

        String[] returnColumns = { "ID" };

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, returnColumns)) {

            ps.setLong(1, studyPlanType.getId());
            ps.setLong(2, user.getId());
            ps.setString(3, studyPlan.getName());
            ps.setString(4, studyPlan.getDescription());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                studyPlan.setId(rs.getLong(1));
            }

            log.info("Plano de estudo criado com sucesso!");

        } catch (SQLException e) {
            log.error("Erro ao inserir Plano de Estudo.", e);
            throw new RuntimeException("Erro ao inserir Plano de Estudo.", e);
        }

    }

    // Lista os planos de estudo do usuário
    public List<StudyPlan> listUserStudyPlans(long idUser) {
        // Cria a lista que vai armazenar os planos de estudos
        List<StudyPlan> studyPlans = new ArrayList<>();

        // Adicionando os JOINs para buscar os dados de Usuario e Tipo de Plano
        String sql = "SELECT sp.id, sp.name, sp.description, " +
                "spt.id AS spt_id, spt.name AS spt_name, spt.description AS spt_description, " +
                "u.id AS u_id, u.username AS u_username " +
                "FROM DB_STUDY_PLANS sp " +
                "INNER JOIN DB_STUDY_PLAN_TYPES spt ON sp.id_study_plan_type = spt.id " +
                "INNER JOIN DB_USERS u ON sp.id_user = u.id " +
                "WHERE sp.id_user = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            // Passando para o 'where' o id que vem no parâmetro
            ps.setLong(1, idUser);

            ResultSet rs = ps.executeQuery();

            /* Pegando os valores dos atributos do banco
            e passando para os atributos do java */
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");

                // Populando o StudyPlanType completo
                long idStudyPlanType = rs.getLong("spt_id");
                String sptName = rs.getString("spt_name");
                String sptDescription = rs.getString("spt_description");
                StudyPlanType studyPlanType =
                        new StudyPlanType(idStudyPlanType, sptName, sptDescription);

                // Populando o User completo
                long userId = rs.getLong("u_id");
                String username = rs.getString("u_username");
                User user = new User(userId, username);

                studyPlans.add(
                        new StudyPlan(id, studyPlanType, user, name, description));
            }

        } catch (SQLException e) {
            log.error("Erro ao listar planos de estudo do usuário.", e);
            throw new RuntimeException("Erro ao listar planos de estudo do usuário.", e);
        }
        return studyPlans;
    }

    // Exclui um plano de estudo
    public int deleteStudyPlan(long id){

        String sql = "DELETE FROM DB_STUDY_PLANS WHERE ID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                log.info("Plano de estudo excluído com sucesso!");
            } else {
                log.info("Nenhum plano de estudo encontrado para exclusão. ID: {}", id);
            }

            return rows;

        } catch (SQLException e) {
            log.error("Não foi possível excluir o plano de estudo.", e);
            throw new RuntimeException("Não foi possível excluir o plano de estudo.", e);
        }
    }

}
