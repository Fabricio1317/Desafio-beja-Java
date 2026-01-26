package com.becajava.ms_transaction_worker.core.usecase;

import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway; // Adicionei o Gateway que faltava
import com.becajava.ms_transaction_worker.infra.dto.ContaExternaDTO;
import com.becajava.ms_transaction_worker.infra.dto.UsuarioDTO;
import com.becajava.ms_transaction_worker.infra.integration.MockApiFinanceiroClient;
import com.becajava.ms_transaction_worker.infra.integration.UsuarioClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarTransacaoSucessoTest {

    @Mock private TransacaoGateway transacaoGateway;
    @Mock private UsuarioClient usuarioClient;
    @Mock private MockApiFinanceiroClient mockApiFinanceiroClient;
    @Mock private ValidadorGateway validadorGateway; // Precisa desse mock também!

    @InjectMocks
    private ProcessarTransacaoUseCase useCase;

    @Test
    @DisplayName("SUCESSO: Deve debitar do pagador e creditar no recebedor corretamente")
    void deveAprovarTransferencia() {
        // --- CENÁRIO ---
        Long idPagador = 1L;
        Long idRecebedor = 2L;

        // Crie a transação USANDO AS VARIÁVEIS DE ID
        Transacao transacao = new Transacao(
                UUID.randomUUID(),
                idPagador, // ID 1
                idRecebedor, // ID 2
                BigDecimal.valueOf(100),
                "PENDENTE",
                "TRANSFERENCIA"
        );

        // --- MOCKS ---

        // Mock do Pagador (ID 1)
        UsuarioDTO pagadorDto = new UsuarioDTO(idPagador, "Daniel", "12345678900", "daniel@email.com", BigDecimal.ZERO);
        when(usuarioClient.buscarPorId(idPagador)).thenReturn(pagadorDto);

        // Mock do Recebedor (ID 2) - IMPORTANTE!
        // Seu código busca o recebedor também: "var recebedor = ... usuarioClient.buscarPorId(transacao.getRecebedorId())"
        UsuarioDTO recebedorDto = new UsuarioDTO(idRecebedor, "Recebedor", "98765432100", "recebedor@email.com", BigDecimal.ZERO);
        when(usuarioClient.buscarPorId(idRecebedor)).thenReturn(recebedorDto);

        // Mock dos Saldos no Banco Central
        when(mockApiFinanceiroClient.buscarConta(idPagador)).thenReturn(new ContaExternaDTO(idPagador, BigDecimal.valueOf(500)));
        when(mockApiFinanceiroClient.buscarConta(idRecebedor)).thenReturn(new ContaExternaDTO(idRecebedor, BigDecimal.ZERO));

        // Mock do Dólar (Seu código chama validadorGateway.obterCotacaoDolar())
        when(validadorGateway.obterCotacaoDolar()).thenReturn(5.0);

        // --- EXECUÇÃO ---
        useCase.execute(transacao);

        // --- VALIDAÇÃO ---

        // Verifica atualização no Banco Central
        verify(mockApiFinanceiroClient).atualizarSaldoExterno(eq(idPagador), argThat(dto -> dto.saldo().compareTo(BigDecimal.valueOf(400)) == 0));
        verify(mockApiFinanceiroClient).atualizarSaldoExterno(eq(idRecebedor), argThat(dto -> dto.saldo().compareTo(BigDecimal.valueOf(100)) == 0));

        // Verifica atualização no Banco Local (MS-User)
        verify(usuarioClient).atualizarSaldo(eq(idPagador), eq(BigDecimal.valueOf(-100))); // Seu código faz "saldo - valor". Se o saldo do DTO mockado é ZERO, fica negativo.
        // DICA: No mock do UsuarioDTO, coloque saldo 500 se quiser que fique positivo no banco local também.

        // Verifica se salvou como APROVADA
        verify(transacaoGateway).atualizar(argThat(t -> t.getStatus().equals("APROVADA")));
    }
}