package br.com.fiap.study_manager.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private long id;
    private String username;

    public User(String username) {
        this.username = username;
    }

    public User(long id) {
        this.id = id;
    }

}
