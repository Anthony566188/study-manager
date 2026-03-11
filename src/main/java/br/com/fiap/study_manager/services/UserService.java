package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.User;
import br.com.fiap.study_manager.models.UserAuth;
import br.com.fiap.study_manager.repository.UserAuthRepository;
import br.com.fiap.study_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;


    public UserAuth addUser(UserAuth userAuth){
        // Capturando o usuário a partir do login passado no paramêtro
        User user = userAuth.getUser();

        // Cadastrando usuário no banco
        userRepository.registerUser(user);

        // Cadastrando o login
        userAuthRepository.registerAuth(userAuth);

        return userAuth;
    }

}
