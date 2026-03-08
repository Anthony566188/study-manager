//package br.com.fiap.study_manager.view;
//
//import br.com.fiap.study_manager.StudyManagerApplication;
//import br.com.fiap.study_manager.models.StudyPlanType;
//import br.com.fiap.study_manager.repository.StudyPlanTypeRepository;
//import org.springframework.boot.SpringApplication;
//import org.springframework.context.ApplicationContext;
//
//import java.sql.SQLException;
//
//public class InsertStudyStrategy {
//
//    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//
//
//        // 1. Inicia o contexto do Spring para ler o application.properties e configurar o Oracle
//        ApplicationContext context = SpringApplication.run(StudyManagerApplication.class, args);
//
//        // 2. Em vez de dar 'new', pede ao Spring a instância do repositório já configurada
//        StudyPlanTypeRepository studyPlanTypeRepository = context.getBean(StudyPlanTypeRepository.class);
//
//        // 3. Cria o objeto de teste
//        StudyPlanType studyPlanType = new StudyPlanType("Rotina Semanal", null);
//
//        // 4. Tenta salvar
//        System.out.println("Iniciando teste de inserção...");
//        studyPlanTypeRepository.insertStudyStrategy(studyPlanType);
//
//        System.out.println("Teste finalizado. Verifique o log acima.");
//    }
//
//}
