package com.becajava.ms_transaction_worker.infra.listener;

import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.usecase.ProcessarTransacaoUseCase;
import com.becajava.ms_transaction_worker.infra.dto.TransacaoKafkaDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoListenerTest {

    @Mock
    private ProcessarTransacaoUseCase processarTransacaoUseCase;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransacaoListener transacaoListener;

    @Test
    void deveProcessarMensagemComSucesso() throws JsonProcessingException {
        String mensagemJson = "{\"id\": \"123\", \"valor\": 100}";

        TransacaoKafkaDTO dtoReal = new TransacaoKafkaDTO(UUID.randomUUID(), 1L, 2L, new BigDecimal("100.00"), "PENDENTE", "TRANSFERENCIA");

        when(objectMapper.readValue(anyString(), eq(TransacaoKafkaDTO.class))).thenReturn(dtoReal);

        transacaoListener.ouvir(mensagemJson);

        verify(processarTransacaoUseCase, times(1)).execute(any(Transacao.class));
    }

    @Test
    void deveTratarErroJsonInvalido() throws JsonProcessingException {
        String jsonRuim = "conteudo invalido";

        when(objectMapper.readValue(anyString(), eq(TransacaoKafkaDTO.class)))
                .thenThrow(new RuntimeException("Erro de parse JSON"));

        transacaoListener.ouvir(jsonRuim);

        verify(processarTransacaoUseCase, never()).execute(any());
    }
}