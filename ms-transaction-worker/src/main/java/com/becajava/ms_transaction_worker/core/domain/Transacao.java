package com.becajava.ms_transaction_worker.core.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transacao {
    private UUID id;
    private Long usuarioId;
    private BigDecimal valor;
    private StatusTransacao status;
    private String tipo;      // RECEITA ou DESPESA
    private String categoria;
    private String descricao;

    public Transacao() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public StatusTransacao getStatus() { return status; }
    public void setStatus(StatusTransacao status) { this.status = status; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}