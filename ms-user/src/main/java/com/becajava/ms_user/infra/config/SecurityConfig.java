package com.becajava.ms_user.infra.config;

import com.becajava.ms_user.infra.config.SecurityFilter; // Importe o seu filtro corretamente
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Desabilita CSRF (Essencial para APIs REST funcionarem via Postman/Front-end)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Define como Stateless (Não guarda sessão, usa apenas Token)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Regras de Acesso
                .authorizeHttpRequests(auth -> auth
                        // --- PÚBLICOS (Não precisa de Token) ---
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll() // Criar conta
                        .requestMatchers(HttpMethod.GET, "/users/{id}").permitAll() // Consultar ID (Usado pelo MS-Transaction)

                        // Deixei liberado aqui para o futuro (se instalar o Swagger, já funciona)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // --- PROTEGIDOS (Precisa de Token) ---
                        // Qualquer outra rota (incluindo /users/importar) cai aqui
                        .anyRequest().authenticated()
                )

                // 4. Adiciona o filtro de Token antes do filtro de senha padrão
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}