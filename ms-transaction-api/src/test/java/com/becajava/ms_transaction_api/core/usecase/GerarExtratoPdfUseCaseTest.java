package com.becajava.ms_transaction_api.core.usecase;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.exception.RegraDeNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GerarExtratoPdfUseCaseTest {

    private final GerarExtratoPdfUseCase useCase = new GerarExtratoPdfUseCase();

    @Test
    @DisplayName("Deve gerar bytes do PDF quando houver transações")
    void deveGerarPdf() {
        Transacao t = new Transacao();
        t.setValor(new BigDecimal("50.0"));
        t.setDataCriacao(LocalDateTime.now());
        t.setTipo("RECEITA");

        byte[] resultado = useCase.gerar(List.of(t));

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }

    @Test
    @DisplayName("Deve falhar ao tentar gerar PDF com lista vazia")
    void deveFalharListaVazia() {
        assertThrows(RegraDeNegocioException.class, () -> useCase.gerar(List.of()));
    }
}