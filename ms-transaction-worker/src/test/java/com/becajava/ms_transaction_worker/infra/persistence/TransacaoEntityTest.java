package com.becajava.ms_transaction_worker.infra.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

class TransacaoEntityTest {

    @Test
    void deveCriarEntidadeComConstrutorCheio() {
        UUID id = UUID.randomUUID();
        Long pagadorId = 1L;
        Long recebedorId = 2L;
        BigDecimal valor = new BigDecimal("500.00");
        String status = "PENDENTE";
        String tipo = "TRANSFERENCIA";

        TransacaoEntity entity = new TransacaoEntity(id, pagadorId, recebedorId, status, valor, tipo);

        Assertions.assertEquals(id, entity.getId());
        Assertions.assertEquals(pagadorId, entity.getPagadorId());
        Assertions.assertEquals(recebedorId, entity.getRecebedorId());
        Assertions.assertEquals(valor, entity.getValor());
        Assertions.assertEquals(status, entity.getStatus());
        Assertions.assertEquals(tipo, entity.getTipo());
    }

    @Test
    void deveTestarGettersESetters() {
        TransacaoEntity entity = new TransacaoEntity();
        UUID id = UUID.randomUUID();

        entity.setId(id);
        entity.setPagadorId(10L);
        entity.setStatus("APROVADO");
        entity.setValor(BigDecimal.TEN);
        entity.setTipo("DEPOSITO");

        Assertions.assertEquals(id, entity.getId());
        Assertions.assertEquals(10L, entity.getPagadorId());
        Assertions.assertEquals("APROVADO", entity.getStatus());
        Assertions.assertEquals(BigDecimal.TEN, entity.getValor());
        Assertions.assertEquals("DEPOSITO", entity.getTipo());
    }
}