package com.becajava.ms_transaction_api.infra.persistence;

import com.becajava.ms_transaction_api.core.domain.StatusTransacao;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private StatusTransacao status;

    private String tipo;
    private String categoria;
    private String descricao;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;


    public TransacaoEntity() {}


    public TransacaoEntity(UUID id, Long usuarioId, StatusTransacao status, BigDecimal valor, String tipo, String categoria, String descricao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.status = status;
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.descricao = descricao;
        this.dataCriacao = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public BigDecimal getValor() { return valor; }
    public StatusTransacao getStatus() { return status; }
    public String getTipo() { return tipo; }
    public String getCategoria() { return categoria; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }


    public void setId(UUID id) { this.id = id; }
    public void setStatus(StatusTransacao status) { this.status = status; }
}