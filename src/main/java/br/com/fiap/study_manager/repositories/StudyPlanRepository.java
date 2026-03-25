package br.com.fiap.study_manager.repositories;

import br.com.fiap.study_manager.models.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

    // O Spring gera o SELECT automaticamente baseado no nome do método
    List<StudyPlan> findByUserId(Long userId);

}
