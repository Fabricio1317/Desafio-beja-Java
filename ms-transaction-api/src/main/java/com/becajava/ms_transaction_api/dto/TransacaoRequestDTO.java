package com.becajava.ms_transaction_api.dto;

import java.math.BigDecimal;

public record TransacaoRequestDTO(
        Long pagadorId,
        Long recebedorId,
        BigDecimal valor,
        String tipo
) {
}
