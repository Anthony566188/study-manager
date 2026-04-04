package br.com.fiap.study_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Necessário para permitir métodos POST
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/users").permitAll() // Libera a criação de usuários
                        .requestMatchers(HttpMethod.POST, "/auth").permitAll()  // Libera o login
                        .requestMatchers(HttpMethod.GET, "/health").permitAll()
                        .anyRequest().authenticated() // Exige autenticação para todo o resto
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}