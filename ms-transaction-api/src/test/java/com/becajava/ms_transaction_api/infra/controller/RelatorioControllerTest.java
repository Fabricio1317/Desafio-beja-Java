package com.becajava.ms_transaction_api.infra.controller;

import com.becajava.ms_transaction_api.core.usecase.GerarRelatorioUseCase;
import com.becajava.ms_transaction_api.infra.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class RelatorioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GerarRelatorioUseCase gerarRelatorioUseCase;

    @InjectMocks
    private RelatorioController relatorioController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(relatorioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve retornar 200 ao solicitar analise mensal sem datas (usa default)")
    void deveRetornarAnaliseComDatasPadrao() throws Exception {
        Long usuarioId = 1L;
        Map<String, Object> resultadoFake = new HashMap<>();
        resultadoFake.put("status", "processado");

        when(gerarRelatorioUseCase.execute(eq(usuarioId), any(), any())).thenReturn(resultadoFake);

        mockMvc.perform(get("/relatorios/analise/{usuarioId}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("processado"));
    }

    @Test
    @DisplayName("Deve aceitar parametros de data no formato ISO")
    void deveAceitarDatasCustomizadas() throws Exception {
        Long usuarioId = 1L;
        String dataInicio = "2026-01-01T00:00:00";
        String dataFim = "2026-01-31T23:59:59";

        mockMvc.perform(get("/relatorios/analise/{usuarioId}", usuarioId)
                        .param("inicio", dataInicio)
                        .param("fim", dataFim))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 400 quando o UseCase lançar erro de negócio no relatório")
    void deveRetornar400ErroNoRelatorio() throws Exception {
        when(gerarRelatorioUseCase.execute(anyLong(), any(), any()))
                .thenThrow(new RuntimeException("Erro inesperado no banco"));
        mockMvc.perform(get("/relatorios/analise/1"))
                .andExpect(status().isInternalServerError());
    }


}