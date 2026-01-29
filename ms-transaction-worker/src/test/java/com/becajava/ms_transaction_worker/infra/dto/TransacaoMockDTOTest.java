package com.becajava.ms_transaction_worker.infra.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransacaoMockDTOTest {

    @Test
    @DisplayName("Record TransacaoMockDTO deve mapear campos corretamente")
    void testeRecord() {
        TransacaoMockDTO dto = new TransacaoMockDTO(10L, 20L, 500.0, "TRANSFERENCIA", "2024-01-01");

        assertEquals(10L, dto.pagadorId());
        assertEquals(20L, dto.recebedorId());
        assertEquals(500.0, dto.valor());
        assertEquals("TRANSFERENCIA", dto.tipo());
        assertEquals("2024-01-01", dto.data());
    }
}