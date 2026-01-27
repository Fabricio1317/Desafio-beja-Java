package com.becajava.ms_transaction_api.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transacao {
    private UUID id;
    private Long usuarioId;
    private BigDecimal valor;
    private String tipo;
    private String categoria;
    private String descricao;

    private StatusTransacao status;
    private LocalDateTime dataCriacao;

    public Transacao() {}

    public Transacao(UUID id, Long usuarioId, BigDecimal valor, String tipo, String categoria, String descricao, StatusTransacao status, LocalDateTime dataCriacao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.descricao = descricao;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public StatusTransacao getStatus() { return status; }
    public void setStatus(StatusTransacao status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}