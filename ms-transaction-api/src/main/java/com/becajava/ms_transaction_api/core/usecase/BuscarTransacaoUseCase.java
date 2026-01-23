package com.becajava.ms_transaction_api.core.usecase;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_api.dto.TransacaoRespondeDTO;

import java.util.UUID;

public class BuscarTransacaoUseCase {

    private final TransacaoGateway transacaoGateway;

    public BuscarTransacaoUseCase(TransacaoGateway transacaoGateway) {
        this.transacaoGateway = transacaoGateway;
    }

    public TransacaoRespondeDTO execute(UUID id){
        Transacao transacao = transacaoGateway.buscaPorId(id).orElseThrow(()->new IllegalArgumentException("Transição não encontrada"));
        return new TransacaoRespondeDTO(transacao);
    }
}
