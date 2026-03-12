package br.com.fiap.study_manager.controllers;

import br.com.fiap.study_manager.models.UserAuth;
import br.com.fiap.study_manager.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserAuth addUser(@RequestBody UserAuth userAuth){ //Binding do JSON para o objeto User
        log.info("Cadastrando usuário...");
        return service.addUser(userAuth);
    }

}
