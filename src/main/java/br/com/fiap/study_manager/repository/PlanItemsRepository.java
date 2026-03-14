package br.com.fiap.study_manager.repository;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.models.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.INTEGER;

@Repository
public class PlanItemsRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource; // O Spring injeta a conexão do application.properties aqui

    // Insere no plano de estudo
    public void insertPlanItem(PlanItem planItem) {

        // Obter o objeto StudyPlan a partir do planItem
        StudyPlan studyPlan = planItem.getStudyPlan();
        Subject subject = planItem.getSubject();

        String sql = "INSERT INTO DB_PLAN_ITEMS " +
                "(id_study_plan, id_subject, custom_title, weekday, start_time, duration_minutes) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        String[] returnColumns = { "ID" };

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, returnColumns)) {

            ps.setLong(1, studyPlan.getId());
            ps.setNull(2, INTEGER); // Apenas para teste
            ps.setString(3, planItem.getCustomTitle());
            ps.setString(4, planItem.getWeekday());
            ps.setTime(5, Time.valueOf(planItem.getStartTime()));
            ps.setInt(6, planItem.getDurationMinutes());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                planItem.setId(rs.getLong(1));
            }

            log.info("Item do plano de estudo inserido com sucesso!");

        } catch (Exception e) {
            log.error("Erro ao inserir item no plano de estudo: " + e.getMessage());
        }
    }

    // Listar os itens do plano de estudo
//    public List<PlanItem> listItemsPLan(long idStudyPlan) {
//
//        // Cria a lista que vai armazenar os itens do plano de estudo
//        List<PlanItem> planItems = new ArrayList<>();
//
//        String sql = "SELECT * FROM DB_PLAN_ITEMS WHERE id_study_plan = ?";
//
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)){
//
//            // Passando para o 'where' o id do plano de estudo que vem no parâmetro
//            ps.setLong(1, idStudyPlan);
//
//            ResultSet rs = ps.executeQuery();
//
//            /* Pegando os valores dos atributos do banco
//            e passando para os atributos do java */
//            while (rs.next()) {
//                long id = rs.getLong("id");
//                String weekday = rs.getString("weekday");
//                LocalTime startTime = rs.getTime("start_time").toLocalTime();
//                Integer durationMinutes = rs.getInt("duration_minutes");
//
//                StudyPlan studyPlan = new StudyPlan(idStudyPlan);
//
//                planItems.add(
//                        new PlanItem(id, studyPlan, weekday, startTime, durationMinutes));
//            }
//
//        } catch (SQLException e) {
//            log.error("Erro ao listar tickets do Paciente.", e);
//        }
//        return planItems;
//    }

    // Exclui um item do plano de estudo
    public void deletePlanItem(long id){

        String sql = "DELETE FROM DB_PLAN_ITEMS WHERE ID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.execute();

            log.info("Item do plano de estudo excluído com sucesso!");

        } catch (SQLException e) {
            log.error("Não foi possível excluir  item do plano de estudo: " + e.getMessage());
        }
    }


}
