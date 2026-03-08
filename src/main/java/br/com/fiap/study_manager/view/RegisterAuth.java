package br.com.fiap.study_manager.view;

import br.com.fiap.study_manager.StudyManagerApplication;
import br.com.fiap.study_manager.models.User;
import br.com.fiap.study_manager.models.UserAuth;
import br.com.fiap.study_manager.repository.UserAuthRepository;
import br.com.fiap.study_manager.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.sql.SQLException;

public class RegisterAuth {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {


        // 1. Inicia o contexto do Spring para ler o application.properties e configurar o Oracle
        ApplicationContext context = SpringApplication.run(StudyManagerApplication.class, args);

        // 2. Em vez de dar 'new', pede ao Spring a instância do repositório já configurada
        UserAuthRepository userAuthRepository = context.getBean(UserAuthRepository.class);

        // 3. Cria o usuário
        User user = new User(1);

        // 4. Cria o objeto de teste
        UserAuth userAuth = new UserAuth(user, "loginAnthony", "senhaAnthony");

        // 5. Tenta salvar
        System.out.println("Iniciando teste de inserção...");
        userAuthRepository.registerAuth(userAuth);

        System.out.println("Teste finalizado. Verifique o log acima.");
    }

}
