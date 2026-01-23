package com.becajava.ms_transaction_worker.infra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrasilApiDTO(
        String moeda,
        Double valor
) {
}