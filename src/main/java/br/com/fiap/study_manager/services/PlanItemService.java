package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.PlanItem;
import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.models.Subject;
import br.com.fiap.study_manager.models.enums.Weekday;
import br.com.fiap.study_manager.repositories.PlanItemsRepository;
import br.com.fiap.study_manager.repositories.StudyPlanRepository;
import br.com.fiap.study_manager.repositories.StudySessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PlanItemService {

    @Autowired
    PlanItemsRepository repository;

    @Autowired
    private StudyPlanRepository studyPlanRepository;

    @Autowired
    private StudySessionRepository studySessionRepository;

    private final StudyBalanceService studyBalanceService;

    public PlanItemService(StudyBalanceService studyBalanceService) {
        this.studyBalanceService = studyBalanceService;
    }

    public PlanItem addPlanItem(PlanItem planItem) {

        // Busca o plano para saber qual é o tipo
        StudyPlan plan = findStudyPlanById(planItem.getStudyPlan().getId());
        String tipoPlano = plan.getStudyPlanType().getName();

        if ("Rotina Semanal".equalsIgnoreCase(tipoPlano) || "Híbrido".equalsIgnoreCase(tipoPlano)) {

            // Verifica se há conflito entre dia e hora
            boolean conflito = repository.existsByStudyPlanIdAndWeekdayAndStartTime(
                    plan.getId(),
                    planItem.getWeekday(),
                    planItem.getStartTime());
            if (conflito) {
                // Retorna 409 Conflict abortando a requisição
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Já existe um item agendado para este dia e horário na sua Rotina Semanal.");
            }

            // Impede que seja enviado null em certos campos
            if (planItem.getWeekday() == null ||
                    planItem.getStartTime() == null ||
                    planItem.getDurationMinutes() == null){

                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Para o plano do tipo 'Rotina Semanal' e 'Híbrido', " +
                                "não é permitido enviar null em 'weekday' e/ou 'startTime'.");

            }

            // Verifica se foi enviado apenas 'subject' ou 'customTitle'
            subjectAndCustomTileValidation(planItem.getSubject(), planItem.getCustomTitle());

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

        if ("Híbrido".equalsIgnoreCase(tipoPlano) && planItem.getSubject() != null) {

            Long idUser = plan.getUser().getId();
            Long idSubject = planItem.getSubject().getId();
            int durationMinutes = planItem.getDurationMinutes();

            studyBalanceService.updateBalance(idUser, idSubject, durationMinutes);
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

        // Retorna o item atualizado
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

        // --- LÓGICA DE ATUALIZAÇÃO DO BANCO DE HORAS (HÍBRIDO) ---
        if ("Híbrido".equalsIgnoreCase(tipoPlano)) {
            Long idUser = plan.getUser().getId();

            // Estorna (subtrai) o tempo do item antigo
            if (existing.getSubject() != null && existing.getDurationMinutes() != null) {
                studyBalanceService.updateBalance(idUser, existing.getSubject().getId(), -existing.getDurationMinutes());
            }

            // Deposita o tempo do item atualizado
            if (planItem.getSubject() != null && planItem.getDurationMinutes() != null) {
                studyBalanceService.updateBalance(idUser, planItem.getSubject().getId(), planItem.getDurationMinutes());
            }
        }

        // Atualiza os dados
        existing.setSubject(planItem.getSubject());
        existing.setCustomTitle(planItem.getCustomTitle());
        existing.setWeekday(planItem.getWeekday());
        existing.setStartTime(planItem.getStartTime());
        existing.setDurationMinutes(planItem.getDurationMinutes());

        if ("Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
            // Se mandou null, vira false. Se mandou valor, usa o valor.
            existing.setDone(planItem.getDone() != null ? planItem.getDone() : false);
        }
        if (!"Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
            existing.setDone(null);
        }

        repository.save(existing);

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

    @Transactional
    public void deletePlanItem(Long id) {

        PlanItem itemToExclude = findItemById(id);
        Long planId = itemToExclude.getStudyPlan().getId();

        StudyPlan plan = findStudyPlanById(planId);

        // Se for Híbrido e tiver matéria, estorna o tempo antes de apagar
        if ("Híbrido".equalsIgnoreCase(plan.getStudyPlanType().getName()) && itemToExclude.getSubject() != null) {
            studyBalanceService.updateBalance(
                    plan.getUser().getId(),
                    itemToExclude.getSubject().getId(),
                    -itemToExclude.getDurationMinutes() // Valor negativo para retirar do saldo
            );
        }

        studySessionRepository.deleteByPlanItemId(id); // Deleta as sessões

        repository.deleteById(id); // Deleta o item

    }

    @Transactional
    public void resetDoneByWeekday(Long idStudyPlan, Weekday weekday) {

        StudyPlan plan = findStudyPlanById(idStudyPlan);
        String tipoPlano = plan.getStudyPlanType().getName();

        if (!"Rotina Semanal".equalsIgnoreCase(tipoPlano)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "'done' não se aplica em outro tipo de plano que não seja 'Rotina Semanal'.");
        }

        repository.resetDoneByStudyPlanAndWeekday(idStudyPlan, weekday);

    }

    @Transactional
    public void resetCycle(Long idStudyPlan) {

        StudyPlan plan = findStudyPlanById(idStudyPlan);
        String tipoPlano = plan.getStudyPlanType().getName();

        if (!"Ciclo".equalsIgnoreCase(tipoPlano)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Método 'resetCycle' disponível apenas para plano do tipo 'Ciclo'.");
        }

        repository.resetCycleByStudyPlan(idStudyPlan);

    }

    private void subjectAndCustomTileValidation(Subject subject, String customTitle) {
        if (subject == null && customTitle == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível enviar null em 'subject' e 'customTitle'. " +
                            "Envie pelo menos um.");
        }

        if (subject != null && customTitle != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível enviar 'subject' e 'customTitle' ao mesmo tempo. Envie apenas um.");
        }
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
