package com.becajava.ms_transaction_worker.infra.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BrasilApiDTOTest {

    @Test
    @DisplayName("ask() deve retornar o valor de venda do ÚLTIMO item da lista")
    void askRetornaUltimo() {
        // Cenário: Lista com 3 cotações
        var item1 = new BrasilApiDTO.CotacaoItem(5.00);
        var item2 = new BrasilApiDTO.CotacaoItem(5.10);
        var item3 = new BrasilApiDTO.CotacaoItem(5.20); // O último

        BrasilApiDTO dto = new BrasilApiDTO(List.of(item1, item2, item3));

        // Deve pegar o 5.20
        assertEquals(5.20, dto.ask());
    }

    @Test
    @DisplayName("ask() deve retornar NULL se a lista for nula")
    void askListaNula() {
        BrasilApiDTO dto = new BrasilApiDTO(null);
        assertNull(dto.ask());
    }

    @Test
    @DisplayName("ask() deve retornar NULL se a lista estiver vazia")
    void askListaVazia() {
        BrasilApiDTO dto = new BrasilApiDTO(Collections.emptyList());
        assertNull(dto.ask());
    }

    @Test
    @DisplayName("Deve criar CotacaoItem corretamente")
    void cotacaoItemRecord() {
        BrasilApiDTO.CotacaoItem item = new BrasilApiDTO.CotacaoItem(5.55);
        assertEquals(5.55, item.cotacaoVenda());
    }
}