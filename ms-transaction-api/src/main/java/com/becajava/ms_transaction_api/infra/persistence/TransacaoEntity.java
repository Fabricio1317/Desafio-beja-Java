package com.becajava.ms_transaction_api.infra.persistence;

import com.becajava.ms_transaction_api.core.domain.StatusTransacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransacaoEntity {
    @Id
    private UUID id;
    private Long pagadorId;
    private Long recebedorId;
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    private StatusTransacao status;
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    @Column(name =  "tipo", columnDefinition = "varchar(255)")
    private String tipo;
}
