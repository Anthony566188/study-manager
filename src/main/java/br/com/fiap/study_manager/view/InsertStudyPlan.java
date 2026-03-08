package br.com.fiap.study_manager.view;

import br.com.fiap.study_manager.StudyManagerApplication;
import br.com.fiap.study_manager.models.StudyPlan;
import br.com.fiap.study_manager.models.StudyPlanType;
import br.com.fiap.study_manager.models.User;
import br.com.fiap.study_manager.repository.StudyPlanRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.sql.SQLException;

public class InsertStudyPlan {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {


        // 1. Inicia o contexto do Spring para ler o application.properties e configurar o Oracle
        ApplicationContext context = SpringApplication.run(StudyManagerApplication.class, args);

        // 2. Em vez de dar 'new', pede ao Spring a instância do repositório já configurada
        StudyPlanRepository studyPlanRepository = context.getBean(StudyPlanRepository.class);

        // 3. Criando a estratégia de estudo
        StudyPlanType studyPlanType = new StudyPlanType(1);

        // 4. Criando usuário
        User user = new User(1);

        // 5. Cria o objeto de teste
        StudyPlan studyPlan = new StudyPlan(studyPlanType, user, "Plano_estudo_teste", null);

        // 6. Tenta salvar
        System.out.println("Iniciando teste de inserção...");
        studyPlanRepository.insertStudyPlan(studyPlan);

        System.out.println("Teste finalizado. Verifique o log acima.");
    }

}
