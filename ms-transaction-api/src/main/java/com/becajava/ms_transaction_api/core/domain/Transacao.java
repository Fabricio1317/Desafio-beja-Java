package com.becajava.ms_transaction_api.core.domain;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transacao {
    private UUID id;
    private Long pagadorId;
    private Long recebedorId;
    private BigDecimal valor;
    private StatusTransacao status;
    private LocalDateTime dataCriacao;
    private String tipo;

    public Transacao(Long pagadorId, Long recebedorId, BigDecimal valor, String tipo) {
        this.id = UUID.randomUUID();
        this.dataCriacao = LocalDateTime.now(); // Corrigido para pegar a data atual
        this.pagadorId = pagadorId;
        this.recebedorId = recebedorId;
        this.valor = valor;
        this.tipo = tipo;

        System.out.println("--- DEBUG DO CONSTRUTOR ---");
        System.out.println("Pagador recebido: " + this.pagadorId); System.out.println("Recebedor recebido: " + this.recebedorId);
        System.out.println("São iguais? " + (this.pagadorId != null && this.pagadorId.equals(this.recebedorId)));
        System.out.println("---------------------------");

        this.status = StatusTransacao.PENDENTE;



        validar();
    }

    public Transacao() {
    }

    public Transacao(UUID id, Long pagadorId, Long recebedorId, BigDecimal valor, StatusTransacao status, LocalDateTime dataCriacao, String tipo) {
        this.id = id;
        this.pagadorId = pagadorId;
        this.recebedorId = recebedorId;
        this.valor = valor;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.tipo = tipo;
    }




    private void validar() {
        if (this.valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transação deve ser positivo");
        }
        if ("TRANSFERENCIA".equalsIgnoreCase(this.tipo)) {
            if (this.pagadorId != null && this.pagadorId.equals(this.recebedorId)) {
                throw new IllegalArgumentException("Pagador e recebedor não podem ser o mesmo");
            }
        }
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public UUID getId() {
        return id;
    }

    public Long getPagadorId() {
        return pagadorId;
    }

    public Long getRecebedorId() {
        return recebedorId;
    }

    public StatusTransacao getStatus() {
        return status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setPagadorId(Long pagadorId) {
        this.pagadorId = pagadorId;
    }

    public void setRecebedorId(Long recebedorId) {
        this.recebedorId = recebedorId;
    }

    public void setStatus(StatusTransacao status) {
        this.status = status;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
