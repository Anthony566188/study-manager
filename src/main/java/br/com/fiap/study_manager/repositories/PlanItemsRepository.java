package br.com.fiap.study_manager.repositories;

import br.com.fiap.study_manager.models.PlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanItemsRepository extends JpaRepository<PlanItem, Long> {

    // O Spring Data JPA entende o nome do método e gera o SELECT automaticamente
    List<PlanItem> findByStudyPlanId(Long studyPlanId);

}
