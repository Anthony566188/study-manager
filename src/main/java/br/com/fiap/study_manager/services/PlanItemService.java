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

        // Busca o plano para saber qual é o tipo
        StudyPlan plan = findStudyPlanById(planItem.getStudyPlan().getId());
        String tipoPlano = plan.getStudyPlanType().getName();

        if ("Rotina Semanal".equalsIgnoreCase(tipoPlano) || "Híbrido".equalsIgnoreCase(tipoPlano)) {

            boolean conflito = repository.existsByStudyPlanIdAndWeekdayAndStartTime(
                    plan.getId(),
                    planItem.getWeekday(),
                    planItem.getStartTime()
            );

            if (conflito) {
                // Retorna 409 Conflict abortando a requisição
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Já existe um item agendado para este dia e horário na sua Rotina Semanal.");
            }

            if (planItem.getWeekday() == null || planItem.getStartTime() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Para o plano do tipo 'Rotina Semanal' e 'Híbrido', " +
                                "não é permitido enviar null em 'weekday' e/ou 'startTime'.");
            }

        }

        if ("Ciclo".equalsIgnoreCase(tipoPlano)) {

            if (planItem.getCustomTitle() != null ||
                    planItem.getWeekday() != null ||
                    planItem.getStartTime() != null) {

                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Para o plano do tipo Ciclo, " +
                                "não é permitido enviar 'customTitle', 'weekday' ou 'startTime'. " +
                                "Envie apenas a matéria (subject) e a duração.");
            }

            // Obrigando que a matéria e a duração venham preenchidas!
            if (planItem.getSubject() == null || planItem.getDurationMinutes() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Para o plano do tipo Ciclo, " +
                                "é obrigatório selecionar uma matéria (subject)" +
                                " e definir a duração (durationMinutes).");
            }

            // Se for 'Ciclo', começa com 0 ao invés de 'null'
            planItem.setCompletedMinutes(0);

        }

        // Decide o valor do 'done' baseado no tipo de plano
        if ("Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
            if (planItem.getDone() == null) {
                planItem.setDone(false); // Garante que nasce como false
            }
        }

        // Garante que done seja 'null' se não for do tipo 'Rotina Semanal'
        if (!"Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
            planItem.setDone(null);
        }

        // Salva o item recém-chegado
        PlanItem saved = repository.save(planItem);

        // Dispara o recálculo para arrumar o tempo dele e dos vizinhos
        recalculateDurations(saved.getStudyPlan().getId(), saved.getWeekday());

        // Retorna o item atualizado (buscando do banco para pegar a duração recalculada)
        return findItemById(saved.getId());

    }

    public List<PlanItem> listItems(long idStudyPlan) {

        /*
            Verifica se o plano existe.
            Se não existir, o método já lança o erro 404 automaticamente.
         */
        findStudyPlanById(idStudyPlan);

        // Se a linha de cima não der erro, significa que o plano existe.
        return repository.findByStudyPlanId(idStudyPlan);

    }

    public PlanItem updatePlanItem(Long id, PlanItem planItem) {

        PlanItem existing = findItemById(id);
        Long planId = existing.getStudyPlan().getId();

        StudyPlan plan = findStudyPlanById(planId);
        String tipoPlano = plan.getStudyPlanType().getName();

        if ("Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
            // Só faz a validação de conflito se o usuário estiver TENTANDO MUDAR o dia ou o horário
            if (existing.getWeekday() != planItem.getWeekday() || !existing.getStartTime().equals(planItem.getStartTime())) {

                boolean conflito = repository.existsByStudyPlanIdAndWeekdayAndStartTime(
                        planId,
                        planItem.getWeekday(),
                        planItem.getStartTime()
                );

                if (conflito) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Já existe um item agendado para este dia e horário na sua Rotina Semanal.");
                }
            }
        }

        // --- VALIDAÇÃO PARA O CICLO NO UPDATE ---
        if ("Ciclo".equalsIgnoreCase(tipoPlano)) {
            if (planItem.getSubject() == null || planItem.getDurationMinutes() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Para o plano do tipo Ciclo, é obrigatório manter a matéria (subject) e a duração (durationMinutes).");
            }
        }

        // Se o usuário mudou o dia da semana, precisamos recalcular o dia antigo E o dia novo!
        Weekday oldDay = existing.getWeekday();
        Weekday newDay = planItem.getWeekday();

        // Atualiza os dados
        existing.setSubject(planItem.getSubject());
        existing.setCustomTitle(planItem.getCustomTitle());
        existing.setWeekday(planItem.getWeekday());
        existing.setStartTime(planItem.getStartTime());

        if ("Ciclo".equalsIgnoreCase(tipoPlano)) {
            // Permite que o utilizador altere a meta de tempo no Ciclo
            existing.setDurationMinutes(planItem.getDurationMinutes());

        } else {
            // Força null nos outros planos para limpar qualquer "lixo" que venha no JSON
            existing.setCompletedMinutes(null);
        }

        if ("Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
            // Se mandou null, vira false. Se mandou valor, usa o valor.
            existing.setDone(planItem.getDone() != null ? planItem.getDone() : false);
        }
        if (!"Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
            existing.setDone(null);
        }

        repository.save(existing);

        recalculateDurations(planId, oldDay);
        if (oldDay != newDay) {
            recalculateDurations(planId, newDay);
        }

        return findItemById(id);

    }

    // Alterna o status de concluído (Check/Uncheck)
    public PlanItem toggleDone(Long id) {

        PlanItem existing = findItemById(id);

        StudyPlan plan = findStudyPlanById(existing.getStudyPlan().getId());
        String tipoPlano = plan.getStudyPlanType().getName();

        // Cláusula de Guarda: Se NÃO for Rotina Semanal, lança o erro e para a execução.
        if (!"Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "'done' não se aplica em outro tipo de plano que não seja 'Rotina Semanal'.");
        }

        boolean isDone = existing.getDone() != null && existing.getDone();
        existing.setDone(!isDone);

        return repository.save(existing);

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

    @Transactional
    public void resetDoneByWeekday(Long idStudyPlan, Weekday weekday) {

        // Verifica se o plano existe (se não existir, lança 404 automaticamente)
        StudyPlan plan = findStudyPlanById(idStudyPlan);
        String tipoPlano = plan.getStudyPlanType().getName();

        if (!"Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "'done' não se aplica em outro tipo de plano que não seja 'Rotina Semanal'.");
        }

        repository.resetDoneByStudyPlanAndWeekday(idStudyPlan, weekday);

    }

    private void recalculateDurations(Long idStudyPlan, Weekday weekday) {

        // Descobre qual é o tipo do plano
        StudyPlan plan = findStudyPlanById(idStudyPlan);
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
                // Por enquanto: 60 minutos padrão
                current.setDurationMinutes(60);
            }
        }

        // Salva a lista inteira atualizada no banco
        repository.saveAll(items);
    }

    private PlanItem findItemById(Long id){
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item de id " + id + " não encontrado"));
    }

    private StudyPlan findStudyPlanById(Long id) {
        return studyPlanRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Plano de estudo com id " + id + " não encontrado"));
    }

}
