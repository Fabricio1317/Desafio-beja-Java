package com.becajava.ms_transaction_worker.infra.dto;

import com.becajava.ms_transaction_worker.core.domain.StatusTransacao;
import com.becajava.ms_transaction_worker.core.domain.Transacao;
import java.math.BigDecimal;
import java.util.UUID;

public record TransacaoKafkaDTO(
        UUID id,
        Long usuarioId,
        BigDecimal valor,
        String tipo,
        String categoria,
        String descricao
) {
    public Transacao toDomain() {
        Transacao t = new Transacao();
        t.setId(this.id);
        t.setUsuarioId(this.usuarioId);
        t.setValor(this.valor);
        t.setTipo(this.tipo);
        t.setCategoria(this.categoria);
        t.setDescricao(this.descricao);
        t.setStatus(StatusTransacao.PENDENTE); // Começa sempre Pendente
        return t;
    }
}