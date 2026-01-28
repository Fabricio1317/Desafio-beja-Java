package com.becajava.ms_transaction_worker.core.usecase;

import com.becajava.ms_transaction_worker.core.domain.StatusTransacao;
import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway;
import com.becajava.ms_transaction_worker.infra.dto.ContaExternaDTO;
import com.becajava.ms_transaction_worker.infra.integration.MockApiFinanceiroClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcessarTransacaoUseCaseTest {

    @InjectMocks
    private ProcessarTransacaoUseCase useCase;

    @Mock
    private TransacaoGateway transacaoGateway;

    @Mock
    private ValidadorGateway validadorGateway;

    @Mock
    private MockApiFinanceiroClient financeiroClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve aprovar depósito e atualizar saldo corretamente")
    void deveAprovarDeposito() {

        UUID transacaoId = UUID.randomUUID();

        Transacao transacao = new Transacao();
        transacao.setId(transacaoId);
        transacao.setUsuarioId(1L);
        transacao.setValor(BigDecimal.valueOf(100.0));
        transacao.setTipo("DEPOSITO");
        transacao.setDescricao("Recebimento de Salario");

        ContaExternaDTO contaMock = new ContaExternaDTO("10", 1L, BigDecimal.valueOf(500.0));

        when(validadorGateway.usuarioExiste(1L)).thenReturn(true);
        when(financeiroClient.buscarCarteiraPorUserId(1L)).thenReturn(List.of(contaMock));
        when(validadorGateway.obterCotacaoDolar()).thenReturn(5.0);

        useCase.execute(transacao);

        verify(financeiroClient).atualizarSaldo(eq("10"), argThat(dto ->
                dto.getSaldo().compareTo(BigDecimal.valueOf(600.0)) == 0
        ));

        verify(transacaoGateway).atualizarStatus(eq(transacaoId), eq(StatusTransacao.APROVADA));
    }

    @Test
    @DisplayName("Deve reprovar transação se o valor for negativo")
    void deveReprovarValorNegativo() {
        UUID transacaoId = UUID.randomUUID();
        Transacao transacao = new Transacao();
        transacao.setId(transacaoId);
        transacao.setValor(BigDecimal.valueOf(-50.0));

        useCase.execute(transacao);

        verify(transacaoGateway).atualizarStatus(eq(transacaoId), eq(StatusTransacao.REPROVADA));
        verify(financeiroClient, never()).atualizarSaldo(anyString(), any());
    }
}