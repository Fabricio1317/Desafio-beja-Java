package com.becajava.ms_transaction_worker.infra.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioDTOTest {

    @Test
    @DisplayName("Record UsuarioDTO deve mapear campos corretamente")
    void testeRecord() {
        UsuarioDTO dto = new UsuarioDTO(1L, "12345678900", "email@teste.com", "João");

        assertEquals(1L, dto.id());
        assertEquals("12345678900", dto.cpf());
        assertEquals("email@teste.com", dto.email());
        assertEquals("João", dto.nome());
    }
}