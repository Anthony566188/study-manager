package br.com.fiap.study_manager.view;

import br.com.fiap.study_manager.StudyManagerApplication;
import br.com.fiap.study_manager.models.User;
import br.com.fiap.study_manager.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.sql.SQLException;

public class TestUser {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {


        // 1. Inicia o contexto do Spring para ler o application.properties e configurar o Oracle
        ApplicationContext context = SpringApplication.run(StudyManagerApplication.class, args);

        // 2. Em vez de dar 'new', pede ao Spring a instância do repositório já configurada
        UserRepository userRepository = context.getBean(UserRepository.class);

        // 3. Cria o objeto de teste (usando o construtor que definiste no model)
        User user = new User("Anthony_Teste_Main");

        // 4. Tenta salvar
        System.out.println("Iniciando teste de inserção...");
        userRepository.registerUser(user);

        System.out.println("Teste finalizado. Verifique o log acima.");
    }






}
