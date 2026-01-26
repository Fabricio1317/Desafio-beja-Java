package com.becajava.ms_transaction_worker.infra.dto;

import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <--- IMPORTANTE
import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransacaoKafkaDTO(
        UUID id,
        Long pagadorId,
        Long recebedorId,
        BigDecimal valor,
        String status,
        String tipo
) {

    public Transacao toDomain(){
        Transacao transacao = new Transacao();
        transacao.setId(this.id);
        transacao.setPagadorId(this.pagadorId);
        transacao.setRecebedorId(this.recebedorId);
        transacao.setValor(this.valor);
        transacao.setStatus(this.status);
        transacao.setTipo(this.tipo);
        return transacao;
    }
}