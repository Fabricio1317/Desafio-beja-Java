package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.exception.RegraNegocioException;
import com.becajava.ms_user.core.gateway.UsuarioGateway;

public class DeletarUseCase {
    private final UsuarioGateway usuarioGateway;

    public DeletarUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }

    public void execute(Long id){
        if (usuarioGateway.buscaPorId(id).isEmpty()){
            throw new RegraNegocioException("Usuario nao encontrado");
        }
        usuarioGateway.deletar(id);
    }
}