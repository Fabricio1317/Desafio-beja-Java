package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.exception.RegraNegocioException;
import com.becajava.ms_user.core.gateway.PasswordEncoderGateway;
import com.becajava.ms_user.core.gateway.TokenGateway;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import com.becajava.ms_user.dto.LoginRequestDTO;
import com.becajava.ms_user.dto.LoginResponseDTO;

public class LoginUsuarioUseCase {
    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoderGateway passwordEncoderGateway;
    private final TokenGateway tokenGateway;

    public LoginUsuarioUseCase(UsuarioGateway usuarioGateway, PasswordEncoderGateway passwordEncoderGateway, TokenGateway tokenGateway) {
        this.usuarioGateway = usuarioGateway;
        this.passwordEncoderGateway = passwordEncoderGateway;
        this.tokenGateway = tokenGateway;
    }

    public LoginResponseDTO execute(LoginRequestDTO dto){
        Usuario usuario = usuarioGateway.buscaPorEmail(dto.email())
                .orElseThrow(() -> new RegraNegocioException("Usuario ou senha invalidos"));

        if (!passwordEncoderGateway.matches(dto.senha(), usuario.getSenha())){
            throw new RegraNegocioException("Usuario ou senha invalidos");
        }

        String token = tokenGateway.gerarToken(usuario);

        return new LoginResponseDTO(token);
    }
}