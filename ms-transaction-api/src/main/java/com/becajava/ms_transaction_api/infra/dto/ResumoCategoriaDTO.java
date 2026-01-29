package com.becajava.ms_transaction_api.infra.dto;

import java.math.BigDecimal;

public record ResumoCategoriaDTO(
        String categoria,
        BigDecimal total
) {
}
