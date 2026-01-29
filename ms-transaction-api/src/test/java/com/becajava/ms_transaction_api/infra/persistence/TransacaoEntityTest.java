package com.becajava.ms_transaction_api.infra.persistence;

import com.becajava.ms_transaction_api.core.domain.StatusTransacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransacaoEntityTest {

    @Test
    @DisplayName("Deve instanciar a entidade corretamente via construtor")
    void deveInstanciarPeloConstrutor() {
        UUID id = UUID.randomUUID();
        Long usuarioId = 10L;
        StatusTransacao status = StatusTransacao.PENDENTE;
        BigDecimal valor = new BigDecimal("250.00");
        String tipo = "DESPESA";
        String categoria = "LAZER";
        String descricao = "Cinema";

        TransacaoEntity entity = new TransacaoEntity(id, usuarioId, status, valor, tipo, categoria, descricao);


        assertEquals(id, entity.getId());
        assertEquals(usuarioId, entity.getUsuarioId());
        assertEquals(status, entity.getStatus());
        assertEquals(valor, entity.getValor());
        assertEquals(tipo, entity.getTipo());
        assertEquals(categoria, entity.getCategoria());
        assertEquals(descricao, entity.getDescricao());
        assertNotNull(entity.getDataCriacao());
    }

    @Test
    @DisplayName("Deve permitir alterar apenas ID e Status via setters")
    void deveTestarSettersPermitidos() {
        TransacaoEntity entity = new TransacaoEntity();
        UUID novoId = UUID.randomUUID();

        entity.setId(novoId);
        entity.setStatus(StatusTransacao.PENDENTE);

        assertEquals(novoId, entity.getId());
        assertEquals(StatusTransacao.PENDENTE, entity.getStatus());
    }
}