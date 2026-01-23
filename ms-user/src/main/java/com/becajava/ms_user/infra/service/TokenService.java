package com.becajava.ms_user.infra.service;


import com.auth0.jwt.JWT; // <--- Esse cara que traz o .create()
import com.auth0.jwt.algorithms.Algorithm; // <--- Esse substitui o da jose4j
import com.auth0.jwt.exceptions.JWTCreationException;
import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.gateway.TokenGateway;
import org.springframework.beans.factory.annotation.Value; // <--- Esse é o @Value certo
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
public class TokenService implements TokenGateway {

    @Value("${api.security.token.secret}")
    private String secret;

    @Override
    public String gerarToken(Usuario usuario) {
        try {
            // Define o algoritmo de criptografia usando a sua senha secreta
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Cria o token
            return JWT.create()
                    .withIssuer("ms-user")
                    .withSubject(usuario.getEmail())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);

        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }


    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}