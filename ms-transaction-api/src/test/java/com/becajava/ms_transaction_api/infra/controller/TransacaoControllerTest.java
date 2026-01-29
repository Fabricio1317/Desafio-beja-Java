package com.becajava.ms_transaction_api.infra.controller;

import com.becajava.ms_transaction_api.core.exception.RegraDeNegocioException;
import com.becajava.ms_transaction_api.core.usecase.SolicitarTransacaoUseCase;
import com.becajava.ms_transaction_api.core.usecase.BuscarTransacaoUseCase;
import com.becajava.ms_transaction_api.core.usecase.GerarExtratoPdfUseCase;
import com.becajava.ms_transaction_api.infra.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransacaoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SolicitarTransacaoUseCase solicitarUseCase;

    @Mock
    private BuscarTransacaoUseCase buscarUseCase;

    @Mock
    private GerarExtratoPdfUseCase gerarPdfUseCase;

    @InjectMocks
    private TransacaoController transacaoController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(transacaoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveRetornar201AoCriarTransacao() throws Exception {
        String json = "{\"usuarioId\": 1, \"valor\": 100.0, \"tipo\": \"RECEITA\", \"categoria\": \"S\", \"descricao\": \"D\"}";

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve retornar 400 quando o valor da transação for inválido")
    void deveRetornar400AoCriarTransacaoInvalida() throws Exception {
        doThrow(new RegraDeNegocioException("O valor da transação deve ser maior que zero."))
                .when(solicitarUseCase).execute(any());

        String json = "{\"usuarioId\": 1, \"valor\": 0, \"tipo\": \"RECEITA\"}";

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando não houver transações para gerar PDF")
    void deveRetornar400AoExportarPdfVazio() throws Exception {
        when(buscarUseCase.buscarTodasPorUsuario(1L))
                .thenThrow(new RegraDeNegocioException("Nenhuma transação encontrada para este usuário."));

        mockMvc.perform(get("/transacoes/exportar/1"))
                .andExpect(status().isBadRequest());
    }
}
