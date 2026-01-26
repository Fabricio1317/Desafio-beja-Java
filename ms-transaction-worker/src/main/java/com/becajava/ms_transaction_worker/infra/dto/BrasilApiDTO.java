package com.becajava.ms_transaction_worker.infra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrasilApiDTO(
        @JsonProperty("cotacao_venda") Double valor,
        @JsonProperty("data_hora_cotacao") String dataHora
) {
}