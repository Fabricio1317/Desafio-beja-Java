package com.becajava.ms_transaction_api.core.usecase;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.exception.RegraDeNegocioException; // <--- Import Novo
import com.becajava.ms_transaction_api.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_api.core.dto.TransacaoRespondeDTO;

import java.util.List;
import java.util.UUID;

public class BuscarTransacaoUseCase {

    private final TransacaoGateway transacaoGateway;

    public BuscarTransacaoUseCase(TransacaoGateway transacaoGateway) {
        this.transacaoGateway = transacaoGateway;
    }

    public TransacaoRespondeDTO execute(UUID id){
        Transacao transacao = transacaoGateway.buscaPorId(id)
                .orElseThrow(() -> new RegraDeNegocioException("Transação não encontrada para o ID informado."));
        return new TransacaoRespondeDTO(transacao);
    }

    public List<Transacao> buscarTodasPorUsuario(Long usuarioId) {
        List<Transacao> transacoes = transacaoGateway.buscarPorUsuarioId(usuarioId);

        if (transacoes.isEmpty()) {
            throw new RegraDeNegocioException("Nenhuma transação encontrada para este usuário. Não é possível gerar o extrato.");
        }

        return transacoes;
    }
}