package com.becajava.ms_transaction_worker.infra.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ContaExternaDTOTest {

    @Test
    @DisplayName("Deve criar via Construtor Com Argumentos")
    void construtorCheio() {
        ContaExternaDTO dto = new ContaExternaDTO("carteira-1", 100L, BigDecimal.TEN);

        assertEquals("carteira-1", dto.getId());
        assertEquals(100L, dto.getUserId());
        assertEquals(BigDecimal.TEN, dto.getSaldo());
    }

    @Test
    @DisplayName("Deve funcionar via Construtor Vazio e Setters")
    void construtorVazioESetters() {
        ContaExternaDTO dto = new ContaExternaDTO();
        dto.setId("carteira-2");
        dto.setUserId(200L);
        dto.setSaldo(BigDecimal.ONE);

        assertEquals("carteira-2", dto.getId());
        assertEquals(200L, dto.getUserId());
        assertEquals(BigDecimal.ONE, dto.getSaldo());
    }
}