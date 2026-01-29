package com.becajava.ms_transaction_worker.infra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContaExternaDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("saldo")
    private BigDecimal saldo;

    public ContaExternaDTO() {}

    public ContaExternaDTO(String id, Long userId, BigDecimal saldo) {
        this.id = id;
        this.userId = userId;
        this.saldo = saldo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}