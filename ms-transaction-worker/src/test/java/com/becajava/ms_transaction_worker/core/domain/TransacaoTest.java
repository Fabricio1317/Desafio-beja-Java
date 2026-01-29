package com.becajava.ms_transaction_worker.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransacaoTest {

    @Test
    @DisplayName("1. Deve gerenciar ID (UUID)")
    void testId() {
        Transacao t = new Transacao();
        UUID id = UUID.randomUUID();
        t.setId(id);
        assertEquals(id, t.getId());
    }

    @Test
    @DisplayName("2. Deve gerenciar UsuarioId (Long)")
    void testUsuarioId() {
        Transacao t = new Transacao();
        t.setUsuarioId(12345L);
        assertEquals(12345L, t.getUsuarioId());
    }

    @Test
    @DisplayName("3. Deve gerenciar Valor (BigDecimal)")
    void testValor() {
        Transacao t = new Transacao();
        t.setValor(new BigDecimal("99.99"));
        assertEquals(new BigDecimal("99.99"), t.getValor());
    }

    @Test
    @DisplayName("4. Deve gerenciar Status (Enum)")
    void testStatus() {
        Transacao t = new Transacao();
        t.setStatus(StatusTransacao.APROVADA);
        assertEquals(StatusTransacao.APROVADA, t.getStatus());

        t.setStatus(StatusTransacao.REPROVADA);
        assertEquals(StatusTransacao.REPROVADA, t.getStatus());
    }

    @Test
    @DisplayName("5. Deve gerenciar Tipo (String)")
    void testTipo() {
        Transacao t = new Transacao();
        t.setTipo("PIX");
        assertEquals("PIX", t.getTipo());
    }

    @Test
    @DisplayName("6. Deve gerenciar Categoria (String)")
    void testCategoria() {
        Transacao t = new Transacao();
        t.setCategoria("LAZER");
        assertEquals("LAZER", t.getCategoria());
    }

    @Test
    @DisplayName("7. Deve gerenciar Descricao (String)")
    void testDescricao() {
        Transacao t = new Transacao();
        t.setDescricao("Cinema");
        assertEquals("Cinema", t.getDescricao());
    }

    @Test
    @DisplayName("8. Objeto recém criado deve ter campos nulos")
    void testEstadoInicial() {
        Transacao t = new Transacao();
        assertNull(t.getId());
        assertNull(t.getStatus());
        assertNull(t.getValor());
    }
}