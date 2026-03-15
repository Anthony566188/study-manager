package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.BusinessException;
import br.com.fiap.study_manager.exceptions.ResourceNotFoundException;
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

        if (login == null || login.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException("Login e senha são obrigatórios");
        }

        // Verifica se o login e senha batem
        long idUser = userAuthRepository.verifyCredentials(login, password);

        if (idUser == -1) {
            throw new ResourceNotFoundException("Credenciais inválidas");
        }

        // Se deu certo, busca os dados completos do usuário
        return userRepository.searchUserById(idUser);
    }

}
