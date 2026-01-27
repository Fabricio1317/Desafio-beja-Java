package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.exception.RegraNegocioException;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import com.becajava.ms_user.dto.UsuarioResponseDTO;

public class BuscarUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;

    public BuscarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }

    public UsuarioResponseDTO execute (Long id){
        Usuario usuario = usuarioGateway.buscaPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado"));

        return new UsuarioResponseDTO(usuario);
    }
}