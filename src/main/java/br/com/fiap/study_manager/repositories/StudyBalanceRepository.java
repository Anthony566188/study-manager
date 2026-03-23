package br.com.fiap.study_manager.repositories;

import br.com.fiap.study_manager.models.StudyBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyBalanceRepository extends JpaRepository<StudyBalance, Long> {

    // Busca o "banco de horas" de uma matéria específica para o usuário logado
    Optional<StudyBalance> findByUserIdAndSubjectId(Long userId, Long subjectId);

    List<StudyBalance> findByUserId(Long User);


}
