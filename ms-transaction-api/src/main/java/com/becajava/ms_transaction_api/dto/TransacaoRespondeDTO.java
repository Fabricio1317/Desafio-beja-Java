package com.becajava.ms_transaction_api.dto;

import com.becajava.ms_transaction_api.core.domain.StatusTransacao;
import com.becajava.ms_transaction_api.core.domain.Transacao;

import java.math.BigDecimal;
import java.util.UUID;

public record TransacaoRespondeDTO(
        UUID id,
        StatusTransacao status,
        BigDecimal valor
) {
    public TransacaoRespondeDTO(Transacao transacao){
        this(transacao.getId(), transacao.getStatus(), transacao.getValor());
    }
}
