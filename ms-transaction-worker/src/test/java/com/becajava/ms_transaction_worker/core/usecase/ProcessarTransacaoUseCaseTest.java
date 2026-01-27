package com.becajava.ms_transaction_worker.core.usecase;

import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway;
import com.becajava.ms_transaction_worker.infra.dto.ContaExternaDTO;
import com.becajava.ms_transaction_worker.infra.dto.UsuarioDTO;
import com.becajava.ms_transaction_worker.infra.integration.MockApiFinanceiroClient;
import com.becajava.ms_transaction_worker.infra.integration.UsuarioClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarTransacaoUseCaseTest {

    @Mock
    private TransacaoGateway transacaoGateway;

    @Mock
    private ValidadorGateway validadorGateway;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private MockApiFinanceiroClient financeiroClient;

    @InjectMocks
    private ProcessarTransacaoUseCase useCase;

    @Test
    void deveAprovarTransferenciaComSaldoSuficiente() {

        Transacao transacao = criarTransacao(1L, 2L, new BigDecimal("100.00"), "TRANSFERENCIA");

        UsuarioDTO pagadorReal = new UsuarioDTO(1L, "12345678900", "daniel@teste.com", "Daniel");
        UsuarioDTO recebedorReal = new UsuarioDTO(2L, "98765432100", "recebedor@teste.com", "Recebedor");

        when(usuarioClient.buscarPorId(1L)).thenReturn(pagadorReal);
        when(usuarioClient.buscarPorId(2L)).thenReturn(recebedorReal);

        ContaExternaDTO carteiraPagador = new ContaExternaDTO("1", 1L, new BigDecimal("500.00"));
        ContaExternaDTO carteiraRecebedor = new ContaExternaDTO("20", 2L, BigDecimal.ZERO);

        when(financeiroClient.buscarCarteiraPorUserId(1L)).thenReturn(List.of(carteiraPagador));
        when(financeiroClient.buscarCarteiraPorUserId(2L)).thenReturn(List.of(carteiraRecebedor));

        when(validadorGateway.obterCotacaoDolar()).thenReturn(5.0);


        useCase.execute(transacao);

        verify(financeiroClient, times(2)).atualizarSaldo(anyString(), any(ContaExternaDTO.class));
        verify(transacaoGateway).atualizarStatus(transacao.getId(), "APROVADA");
    }

    @Test
    void deveReprovarTransferenciaSemSaldo() {
        Transacao transacao = criarTransacao(1L, 2L, new BigDecimal("1000.00"), "TRANSFERENCIA");

        UsuarioDTO pagadorReal = new UsuarioDTO(1L, "12345678900", "daniel@teste.com", "Daniel");
        when(usuarioClient.buscarPorId(1L)).thenReturn(pagadorReal);


        UsuarioDTO recebedorReal = new UsuarioDTO(2L, "98765432100", "recebedor@teste.com", "Recebedor");
        when(usuarioClient.buscarPorId(2L)).thenReturn(recebedorReal);


        ContaExternaDTO carteiraPagador = new ContaExternaDTO("10", 1L, new BigDecimal("50.00"));
        when(financeiroClient.buscarCarteiraPorUserId(1L)).thenReturn(List.of(carteiraPagador));

        ContaExternaDTO carteiraRecebedor = new ContaExternaDTO("20", 2L, BigDecimal.ZERO);
        when(financeiroClient.buscarCarteiraPorUserId(2L)).thenReturn(List.of(carteiraRecebedor));


        useCase.execute(transacao);


        verify(transacaoGateway).atualizarStatus(transacao.getId(), "REPROVADA");


        verify(financeiroClient, never()).atualizarSaldo(anyString(), any());
    }

    private Transacao criarTransacao(Long pagador, Long recebedor, BigDecimal valor, String tipo) {
        Transacao t = new Transacao();
        t.setId(UUID.randomUUID());
        t.setPagadorId(pagador);
        t.setRecebedorId(recebedor);
        t.setValor(valor);
        t.setTipo(tipo);
        return t;
    }
}