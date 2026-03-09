package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.User;
import br.com.fiap.study_manager.repository.UserAuthRepository;
import br.com.fiap.study_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private UserRepository userRepository;

    public User auth(String login, String password) {

        // Verifica se o login e senha batem
        long idUser = userAuthRepository.verifyCredentials(login, password);

        if (idUser == -1) {
            return null; // Login falhou
        }

        // Se deu certo, busca os dados completos do usuário
        return userRepository.searchUserById(idUser);
    }

}
