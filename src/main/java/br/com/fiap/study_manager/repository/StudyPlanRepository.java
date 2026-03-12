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
                studyPlan.setId(rs.getInt(1));
            }

            log.info("Plano de estudo criado com sucesso!");

        } catch (Exception e) {
            log.error("Erro ao inserir Plano de Estudo: " + e.getMessage());
        }

    }

    // Lista os planos de estudo do usuário
    public List<StudyPlan> listUserStudyPlans(long idUser) {
        // Cria a lista que vai armazenar os planos de estudos
        List<StudyPlan> studyPlans = new ArrayList<>();

        String sql = "SELECT * FROM DB_STUDY_PLANS WHERE id_user = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            // Passando para o 'where' o id que vem no parâmetro
            ps.setLong(1, idUser);

            ResultSet rs = ps.executeQuery();

            /* Pegando os valores dos atributos do banco
            e passando para os atributos do java */
            while (rs.next()) {
                long id = rs.getLong("id");
                long idStudyPlanType = rs.getLong("id_study_plan_type");
                String name = rs.getString("name");
                String description = rs.getString("description");

                StudyPlanType studyPlanType = new StudyPlanType(idStudyPlanType);
                User user = new User(idUser);

                studyPlans.add(
                        new StudyPlan(id, studyPlanType, user, name, description));
            }

        } catch (SQLException e) {
            log.error("Erro ao listar tickets do Paciente.", e);
        }
        return studyPlans;
    }

    // Exclui um plano de estudo
    public void deleteStudyPlan(long id){

        String sql = "DELETE FROM TB_TICKET WHERE ID_TICKET = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.execute();

            log.info("Plano de estudo excluído com sucesso!");

        } catch (SQLException e) {
            log.error("Não foi possível excluir o plano de estudo: " + e.getMessage());
        }
    }

}
