package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import java.math.BigDecimal;

public class AtualizarSaldoUseCase {

    private final UsuarioGateway usuarioGateway;

    public AtualizarSaldoUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }

    public void execute(Long id, BigDecimal novoSaldo) {
        // 1. Busca o usuário
        Usuario usuario = usuarioGateway.buscaPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // 2. Atualiza apenas o saldo (Certifique-se que o método setSaldo existe na sua Domain Usuario)
        usuario.setSaldo(novoSaldo);

        // 3. Salva no banco através do gateway
        usuarioGateway.atualizar(usuario);
    }
}