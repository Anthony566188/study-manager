package br.com.fiap.study_manager.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuth {

    private User user;
    private String login;
    private String password;

}
