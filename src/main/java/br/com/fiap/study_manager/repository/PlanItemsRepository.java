package br.com.fiap.study_manager.repository;

import br.com.fiap.study_manager.models.PlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanItemsRepository extends JpaRepository<PlanItem, Long> {
    
}
