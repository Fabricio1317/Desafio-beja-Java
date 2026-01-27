package com.becajava.ms_transaction_worker.infra.persistence;

import com.becajava.ms_transaction_worker.core.domain.StatusTransacao;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
public class TransacaoEntity {
    @Id
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

    public void setStatus(StatusTransacao status) { this.status = status; }
}