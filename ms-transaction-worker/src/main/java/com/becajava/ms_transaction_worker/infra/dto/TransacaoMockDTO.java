package com.becajava.ms_transaction_worker.infra.dto;

public record TransacaoMockDTO(
        Long pagadorId,
        Double valor,
        String tipo,
        String data
) {
}
