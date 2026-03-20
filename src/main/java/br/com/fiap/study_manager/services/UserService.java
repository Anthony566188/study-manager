package br.com.fiap.study_manager.services;

import br.com.fiap.study_manager.models.User;
import br.com.fiap.study_manager.models.UserAuth;
import br.com.fiap.study_manager.repositories.UserAuthRepository;
import br.com.fiap.study_manager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserAuth addUser(UserAuth userAuth) {
        User user = userAuth.getUser();

        // Cadastrando usuário no banco (save popula o id no objeto)
        userRepository.save(user);

        // Cadastrando o login (senha é hasheada antes de persistir)
        UserAuth entity = new UserAuth();
        entity.setUser(user);
        entity.setLogin(userAuth.getLogin());
        entity.setPassword(passwordEncoder.encode(userAuth.getPassword()));
        userAuthRepository.save(entity);

        return userAuth;
    }

}
