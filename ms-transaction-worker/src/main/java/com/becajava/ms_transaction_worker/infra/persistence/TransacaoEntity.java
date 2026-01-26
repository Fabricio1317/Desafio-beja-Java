package com.becajava.ms_transaction_worker.infra.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
public class TransacaoEntity {
    @Id
    private UUID id;
    private Long pagadorId;
    private Long recebedorId;
    private BigDecimal valor;
    private String status;
    @Column(name = "tipo")
    private String tipo;
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    public TransacaoEntity() {
    }

    public TransacaoEntity(UUID id, Long pagadorId, Long recebedorId, String status, BigDecimal valor, String tipo) {
        this.id = id;
        this.pagadorId = pagadorId;
        this.recebedorId = recebedorId;
        this.status = status;
        this.valor = valor;
        this.tipo = tipo;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getPagadorId() {
        return pagadorId;
    }

    public void setPagadorId(Long pagadorId) {
        this.pagadorId = pagadorId;
    }

    public Long getRecebedorId() {
        return recebedorId;
    }

    public void setRecebedorId(Long recebedorId) {
        this.recebedorId = recebedorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
