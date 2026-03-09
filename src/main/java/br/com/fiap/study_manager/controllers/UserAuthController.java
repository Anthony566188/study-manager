package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.User;
import br.com.fiap.study_manager.models.UserAuth;
import br.com.fiap.study_manager.services.UserAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserAuthService service;

    @PostMapping("/auth")
    public User auth(@RequestBody UserAuth credentials){ //Binding do JSON para o objeto User
        log.info("Autenticando usuário...");
        return service.auth(credentials.getLogin(), credentials.getPassword());
    }

}
