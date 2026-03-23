package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.models.enums.Weekday;
import br.com.fiap.study_manager.repositories.PlanItemsRepository;
import br.com.fiap.study_manager.repositories.StudyPlanRepository;
import br.com.fiap.study_manager.repositories.StudySessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PlanItemService {

    @Autowired
    PlanItemsRepository repository;

    @Autowired
    private StudyPlanRepository studyPlanRepository;

    @Autowired
    private StudySessionRepository studySessionRepository;

    public PlanItem addPlanItem(PlanItem planItem) {

        // Salva o item recém-chegado
        PlanItem saved = repository.save(planItem);

        // Dispara o recálculo para arrumar o tempo dele e dos vizinhos
        recalculateDurations(saved.getStudyPlan().getId(), saved.getWeekday());

        // Retorna o item atualizado (buscando do banco para pegar a duração recalculada)
        return findItemById(saved.getId());

    }

    public List<PlanItem> listItems(long idStudyPlan) {

        return repository.findByStudyPlanId(idStudyPlan);

    }

    public PlanItem updatePlanItem(Long id, PlanItem planItem) {

        PlanItem existing = findItemById(id);
        Long planId = existing.getStudyPlan().getId();

        // Se o usuário mudou o dia da semana, precisamos recalcular o dia antigo E o dia novo!
        Weekday oldDay = existing.getWeekday();
        Weekday newDay = planItem.getWeekday();

        // Atualiza os dados
        existing.setSubject(planItem.getSubject());
        existing.setCustomTitle(planItem.getCustomTitle());
        existing.setWeekday(planItem.getWeekday());
        existing.setStartTime(planItem.getStartTime());

        repository.save(existing);

        recalculateDurations(planId, oldDay);
        if (oldDay != newDay) {
            recalculateDurations(planId, newDay);
        }

        return findItemById(id);

    }

    @Transactional // Garante que tudo seja excluído ou nada seja (rollback) em caso de erro
    public void deletePlanItem(Long id) {

        PlanItem itemToExclude = findItemById(id);
        Long planId = itemToExclude.getStudyPlan().getId();
        Weekday day = itemToExclude.getWeekday();

        studySessionRepository.deleteByPlanItemId(id); // Deleta as sessões

        repository.deleteById(id); // Deleta o item

        recalculateDurations(planId, day); // Recalcula a rotina

    }

    private void recalculateDurations(Long idStudyPlan, Weekday weekday) {

        // Descobre qual é o tipo do plano
        StudyPlan plan = studyPlanRepository.findById(idStudyPlan);
        String tipoPlano = plan.getStudyPlanType().getName();

        // Se for Ciclo, ABORTA o cálculo automático, pois o usuário digita a duração na mão
        if ("Ciclo".equalsIgnoreCase(tipoPlano)) {
            return;
        }

        // Pega todos os itens daquele dia ordenados por horário
        List<PlanItem> items = repository.findByStudyPlanIdAndWeekdayOrderByStartTimeAsc(idStudyPlan, weekday);

        if (items.isEmpty()) return;

        // Percorre a lista calculando a diferença de tempo
        for (int i = 0; i < items.size(); i++) {
            PlanItem current = items.get(i);

            // Se NÃO for o último item da lista
            if (i < items.size() - 1) {
                PlanItem next = items.get(i + 1);

                // Calcula a diferença em minutos do atual para o próximo
                long minutes = ChronoUnit.MINUTES.between(current.getStartTime(), next.getStartTime());

                current.setDurationMinutes((int) minutes);
            } else {
                // Se for o ÚLTIMO item do dia, ele não tem um "próximo".
                // Por enquanto: Deixar com 60 minutos padrão
                current.setDurationMinutes(60);
            }
        }

        // 3. Salva a lista inteira atualizada no banco
        repository.saveAll(items);
    }

    private PlanItem findItemById(Long id){
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de id " + id + " não encontrado")
        );
    }

}
