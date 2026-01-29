package com.becajava.ms_transaction_worker.core.usecase;

import com.becajava.ms_transaction_worker.core.domain.StatusTransacao;
import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway;
import com.becajava.ms_transaction_worker.infra.dto.ContaExternaDTO;
import com.becajava.ms_transaction_worker.infra.integration.MockApiFinanceiroClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarTransacaoUseCaseTest {

    @InjectMocks
    private ProcessarTransacaoUseCase useCase;

    @Mock private TransacaoGateway transacaoGateway;
    @Mock private ValidadorGateway validadorGateway;
    @Mock private MockApiFinanceiroClient financeiroClient;


    private Transacao criarTransacao(BigDecimal valor, String tipo) {
        Transacao t = new Transacao();
        t.setId(UUID.randomUUID());
        t.setUsuarioId(100L);
        t.setValor(valor);
        t.setTipo(tipo);
        t.setDescricao("Teste Cobertura Total");
        return t;
    }

    private ContaExternaDTO criarCarteira(BigDecimal saldo) {
        return new ContaExternaDTO("carteira-123", 100L, saldo);
    }

    private void mockFluxoSucesso() {
        when(validadorGateway.usuarioExiste(anyLong())).thenReturn(true);
        when(financeiroClient.buscarCarteiraPorUserId(anyLong()))
                .thenReturn(List.of(criarCarteira(BigDecimal.valueOf(1000.00))));
        when(validadorGateway.obterCotacaoDolar()).thenReturn(5.0);
    }

    @Test
    @DisplayName("1. Valor Nulo -> Deve Reprovar")
    void erroValorNulo() {
        Transacao t = criarTransacao(null, "DESPESA");
        useCase.execute(t);
        verify(transacaoGateway).atualizarStatus(t.getId(), StatusTransacao.REPROVADA);
    }

    @Test
    @DisplayName("2. Valor Zero ou Negativo -> Deve Reprovar")
    void erroValorInvalido() {
        Transacao t = criarTransacao(BigDecimal.ZERO, "DESPESA");
        useCase.execute(t);
        verify(transacaoGateway).atualizarStatus(t.getId(), StatusTransacao.REPROVADA);
    }

    @Test
    @DisplayName("3. Usuário existe (true) -> Fluxo normal")
    void usuarioExiste() {
        mockFluxoSucesso();
        useCase.execute(criarTransacao(BigDecimal.TEN, "DESPESA"));

        verify(financeiroClient).atualizarSaldo(anyString(), any());
        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.APROVADA));
    }

    @Test
    @DisplayName("4. Usuário NÃO existe (false) -> Lança Exception interna, Catch captura e CONTINUA")
    void usuarioNaoExiste() {
        when(validadorGateway.usuarioExiste(anyLong())).thenReturn(false);
        when(financeiroClient.buscarCarteiraPorUserId(anyLong()))
                .thenReturn(List.of(criarCarteira(BigDecimal.valueOf(1000))));

        useCase.execute(criarTransacao(BigDecimal.TEN, "DESPESA"));

        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.APROVADA));
    }

    @Test
    @DisplayName("5. Erro na API de Usuário (Exception) -> Catch captura e CONTINUA")
    void erroApiUsuario() {
        when(validadorGateway.usuarioExiste(anyLong())).thenThrow(new RuntimeException("API User Off"));
        when(financeiroClient.buscarCarteiraPorUserId(anyLong()))
                .thenReturn(List.of(criarCarteira(BigDecimal.valueOf(1000))));

        useCase.execute(criarTransacao(BigDecimal.TEN, "DESPESA"));

        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.APROVADA));
    }


    @Test
    @DisplayName("6. Carteira NULL -> Reprova")
    void carteiraNull() {
        when(validadorGateway.usuarioExiste(anyLong())).thenReturn(true);
        when(financeiroClient.buscarCarteiraPorUserId(anyLong())).thenReturn(null);

        useCase.execute(criarTransacao(BigDecimal.TEN, "DESPESA"));
        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.REPROVADA));
    }

    @Test
    @DisplayName("7. Carteira Vazia -> Reprova")
    void carteiraVazia() {
        when(validadorGateway.usuarioExiste(anyLong())).thenReturn(true);
        when(financeiroClient.buscarCarteiraPorUserId(anyLong())).thenReturn(Collections.emptyList());

        useCase.execute(criarTransacao(BigDecimal.TEN, "DESPESA"));
        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.REPROVADA));
    }

    @Test
    @DisplayName("8. Carteira com ID Nulo -> Reprova")
    void carteiraIdNulo() {
        when(validadorGateway.usuarioExiste(anyLong())).thenReturn(true);
        ContaExternaDTO dtoSemId = new ContaExternaDTO(null, 1L, BigDecimal.TEN);
        when(financeiroClient.buscarCarteiraPorUserId(anyLong())).thenReturn(List.of(dtoSemId));

        useCase.execute(criarTransacao(BigDecimal.TEN, "DESPESA"));
        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.REPROVADA));
    }

    @Test
    @DisplayName("9. Erro na API Financeira -> Reprova")
    void erroApiFinanceira() {
        when(validadorGateway.usuarioExiste(anyLong())).thenReturn(true);
        when(financeiroClient.buscarCarteiraPorUserId(anyLong())).thenThrow(new RuntimeException("Erro API"));

        useCase.execute(criarTransacao(BigDecimal.TEN, "DESPESA"));
        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.REPROVADA));
    }


    @Test
    @DisplayName("10. Cotação Dólar Falha -> Usa Padrão (5.0) e Aprova")
    void dolarFalha() {
        mockFluxoSucesso();
        when(validadorGateway.obterCotacaoDolar()).thenThrow(new RuntimeException("Dolar API Off"));

        useCase.execute(criarTransacao(BigDecimal.TEN, "DESPESA"));
        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.APROVADA));
    }

    @Test
    @DisplayName("11. Cotação Dólar Zero/Negativa -> Usa Padrão (5.0)")
    void dolarZero() {
        mockFluxoSucesso();
        when(validadorGateway.obterCotacaoDolar()).thenReturn(0.0);

        useCase.execute(criarTransacao(BigDecimal.TEN, "DESPESA"));
        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.APROVADA));
    }

    @Test
    @DisplayName("12. Valor Acima do Limite -> Loga Alerta e Aprova")
    void valorAlto() {
        mockFluxoSucesso();
        useCase.execute(criarTransacao(BigDecimal.valueOf(6000), "DESPESA"));
        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.APROVADA));
    }


    @Test
    @DisplayName("13. Tipo DESPESA -> Subtrai Saldo")
    void tipoDespesa() {
        mockFluxoSucesso();
        useCase.execute(criarTransacao(BigDecimal.valueOf(100), "DESPESA"));


        verify(financeiroClient).atualizarSaldo(anyString(), argThat(dto ->
                dto.getSaldo().compareTo(BigDecimal.valueOf(900)) == 0));
    }

    @Test
    @DisplayName("14. Tipo SAQUE -> Subtrai Saldo")
    void tipoSaque() {
        mockFluxoSucesso();
        useCase.execute(criarTransacao(BigDecimal.valueOf(50), "SAQUE"));

        verify(financeiroClient).atualizarSaldo(anyString(), argThat(dto ->
                dto.getSaldo().compareTo(BigDecimal.valueOf(950)) == 0));
    }

    @Test
    @DisplayName("15. Tipo RECEITA -> Soma Saldo")
    void tipoReceita() {
        mockFluxoSucesso();
        useCase.execute(criarTransacao(BigDecimal.valueOf(200), "RECEITA"));

        verify(financeiroClient).atualizarSaldo(anyString(), argThat(dto ->
                dto.getSaldo().compareTo(BigDecimal.valueOf(1200)) == 0));
    }

    @Test
    @DisplayName("16. Tipo DEPOSITO -> Soma Saldo")
    void tipoDeposito() {
        mockFluxoSucesso();
        useCase.execute(criarTransacao(BigDecimal.valueOf(10), "DEPOSITO"));

        verify(financeiroClient).atualizarSaldo(anyString(), argThat(dto ->
                dto.getSaldo().compareTo(BigDecimal.valueOf(1010)) == 0));
    }

    @Test
    @DisplayName("17. Tipo NULO -> Assume DESPESA e Subtrai")
    void tipoNulo() {
        mockFluxoSucesso();
        useCase.execute(criarTransacao(BigDecimal.valueOf(100), null));

        verify(financeiroClient).atualizarSaldo(anyString(), argThat(dto ->
                dto.getSaldo().compareTo(BigDecimal.valueOf(900)) == 0));
    }

    @Test
    @DisplayName("18. Tipo DESCONHECIDO -> Não altera Saldo e Aprova")
    void tipoDesconhecido() {
        mockFluxoSucesso();
        useCase.execute(criarTransacao(BigDecimal.valueOf(100), "TRANSFERENCIA"));

        verify(financeiroClient).atualizarSaldo(anyString(), argThat(dto ->
                dto.getSaldo().compareTo(BigDecimal.valueOf(1000)) == 0));

        verify(transacaoGateway).atualizarStatus(any(UUID.class), eq(StatusTransacao.APROVADA));
    }

    @Test
    @DisplayName("19. Erro ao Salvar REPROVADA -> Não lança exceção (Catch aninhado)")
    void erroSalvarReprovada() {
        Transacao t = criarTransacao(null, "DESPESA");

        doThrow(new RuntimeException("DB Off")).when(transacaoGateway)
                .atualizarStatus(t.getId(), StatusTransacao.REPROVADA);

        useCase.execute(t);

        verify(transacaoGateway).atualizarStatus(t.getId(), StatusTransacao.REPROVADA);
    }
}