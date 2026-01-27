package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.exception.RegraNegocioException;
import com.becajava.ms_user.core.gateway.PasswordEncoderGateway;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import com.becajava.ms_user.dto.UsuarioRequestDTO;
import com.becajava.ms_user.dto.UsuarioResponseDTO;

public class CriarUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoderGateway passwordEncoderGateway;

    public CriarUsuarioUseCase(UsuarioGateway usuarioGateway, PasswordEncoderGateway passwordEncoderGateway) {
        this.usuarioGateway = usuarioGateway;
        this.passwordEncoderGateway = passwordEncoderGateway;
    }

    public UsuarioResponseDTO execute(UsuarioRequestDTO dto){
        if (usuarioGateway.existePorCpf(dto.cpf())){
            throw new RegraNegocioException("Erro: CPF ja cadastrado");
        }
        if (usuarioGateway.existePorEmail(dto.email())){
            throw new RegraNegocioException("Erro: Email ja cadastrado");
        }

        String senhaHash = passwordEncoderGateway.encode(dto.senha());
        Usuario novoUsuario = new Usuario(dto.nome(), dto.cpf(), dto.email(), senhaHash);
        Usuario usuario = usuarioGateway.criarUsuario(novoUsuario);

        return new UsuarioResponseDTO(usuario);
    }
}