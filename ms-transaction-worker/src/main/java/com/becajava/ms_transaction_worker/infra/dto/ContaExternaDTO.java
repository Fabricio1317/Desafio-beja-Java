package com.becajava.ms_transaction_worker.infra.dto;

import java.math.BigDecimal;

public record ContaExternaDTO(
        String id,
        Long userId,
        BigDecimal saldo
) {
}
