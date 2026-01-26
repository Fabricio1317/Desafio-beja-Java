package com.becajava.ms_user.infra.config;


import com.becajava.ms_user.core.gateway.PasswordEncoderGateway;
import com.becajava.ms_user.core.gateway.TokenGateway;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import com.becajava.ms_user.core.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    public CriarUsuarioUseCase criarUsuarioUseCase(UsuarioGateway usuarioGateway, PasswordEncoderGateway passwordEncoderGateway){
        return new CriarUsuarioUseCase(usuarioGateway, passwordEncoderGateway);
    }

    @Bean
    public LoginUsuarioUseCase loginUsuarioUseCase (UsuarioGateway gateway, PasswordEncoderGateway passwordEncoderGateway, TokenGateway tokenGateway){
        return new LoginUsuarioUseCase(gateway, passwordEncoderGateway, tokenGateway);
    }

    @Bean
    public DeletarUseCase deletarUseCase(UsuarioGateway gateway){
        return new DeletarUseCase(gateway);
    }

    @Bean
    BuscarUsuarioUseCase buscarUsuarioUseCase(UsuarioGateway gateway){
        return new BuscarUsuarioUseCase(gateway);
    }
}
