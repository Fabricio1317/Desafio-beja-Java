package com.becajava.ms_transaction_worker.core.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transacao {
    private UUID id;
    private Long pagadorId;
    private Long recebedorId;
    private BigDecimal valor;
    private String status;
    private String tipo;


    public Transacao() {
    }

    public Transacao(UUID id, Long pagadorId, Long recebedorId, BigDecimal valor, String status, String tipo) {
        this.id = id;
        this.pagadorId = pagadorId;
        this.recebedorId = recebedorId;
        this.valor = valor;
        this.status = status;
        this.tipo = tipo;
        validar();
    }

    private void validar() {
        if (this.tipo == null || this.tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo da transação é obrigatório");
        }
        if (this.valor == null || this.valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
    }

    public void aprovar() {
        this.status = "APROVADA";
    }

    public void rejeitar() {
        this.status = "REJEITADA";
    }

    // Getters e Setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getPagadorId() { return pagadorId; }
    public void setPagadorId(Long pagadorId) { this.pagadorId = pagadorId; }
    public Long getRecebedorId() { return recebedorId; }
    public void setRecebedorId(Long recebedorId) { this.recebedorId = recebedorId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}