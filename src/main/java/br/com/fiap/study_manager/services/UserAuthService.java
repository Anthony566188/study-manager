package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.exceptions.BusinessException;
import br.com.fiap.study_manager.exceptions.ResourceNotFoundException;
import br.com.fiap.study_manager.models.User;
import br.com.fiap.study_manager.models.UserAuth;
import br.com.fiap.study_manager.repositories.UserAuthRepository;
import br.com.fiap.study_manager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserAuthService {

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User auth(String login, String password) {
        if (login == null || login.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException("Login e senha são obrigatórios");
        }

        Optional<UserAuth> userAuthOpt = userAuthRepository.findByLogin(login);
        if (userAuthOpt.isEmpty()) {
            throw new ResourceNotFoundException("Credenciais inválidas");
        }

        UserAuth userAuth = userAuthOpt.get();
        if (!passwordEncoder.matches(password, userAuth.getPassword())) {
            throw new ResourceNotFoundException("Credenciais inválidas");
        }

        return userRepository.findById(userAuth.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

}
