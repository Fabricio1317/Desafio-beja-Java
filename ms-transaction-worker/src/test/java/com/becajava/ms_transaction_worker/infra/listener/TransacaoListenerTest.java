package com.becajava.ms_transaction_worker.infra.listener;

import com.becajava.ms_transaction_worker.core.domain.Transacao;
import com.becajava.ms_transaction_worker.core.usecase.ProcessarTransacaoUseCase;
import com.becajava.ms_transaction_worker.infra.dto.TransacaoKafkaDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoListenerTest {

    @InjectMocks
    private TransacaoListener listener;

    @Mock private ProcessarTransacaoUseCase useCase;
    @Mock private ObjectMapper objectMapper;

    // Helper para criar DTO real (evita NullPointer no .toDomain())
    private TransacaoKafkaDTO criarDtoValido() {
        return new TransacaoKafkaDTO(UUID.randomUUID(), 1L, BigDecimal.TEN, "DESPESA", "CAT", "DESC");
    }

    @Test
    @DisplayName("1. Sucesso: JSON Padrão deve processar")
    void jsonPadrao() throws JsonProcessingException {
        String json = "{\"valor\": 10}";
        TransacaoKafkaDTO dto = criarDtoValido();

        when(objectMapper.readValue(json, TransacaoKafkaDTO.class)).thenReturn(dto);

        listener.ouvir(json);

        verify(useCase, times(1)).execute(any(Transacao.class));
    }

    @Test
    @DisplayName("2. Sucesso: JSON com Aspas Duplas (Stringified) deve limpar e processar")
    void jsonComAspas() throws JsonProcessingException {
        String jsonSujo = "\"{\"valor\": 10}\"";
        String jsonLimpo = "{\"valor\": 10}";
        TransacaoKafkaDTO dto = criarDtoValido();

        // Mocka a limpeza da string
        when(objectMapper.readValue(jsonSujo, String.class)).thenReturn(jsonLimpo);
        // Mocka a conversão final
        when(objectMapper.readValue(jsonLimpo, TransacaoKafkaDTO.class)).thenReturn(dto);

        listener.ouvir(jsonSujo);

        verify(objectMapper).readValue(jsonSujo, String.class); // Garante que limpou
        verify(useCase).execute(any(Transacao.class));
    }

    @Test
    @DisplayName("3. Erro: JSON Malformado (Syntax Error) deve ser capturado")
    void jsonMalformado() throws JsonProcessingException {
        String jsonRuim = "{ valor: sem_aspas }"; // JSON inválido

        when(objectMapper.readValue(jsonRuim, TransacaoKafkaDTO.class))
                .thenThrow(new RuntimeException("JsonParseException"));

        // Garante que não explode exception para a thread do Kafka
        assertDoesNotThrow(() -> listener.ouvir(jsonRuim));

        verify(useCase, never()).execute(any());
    }

    @Test
    @DisplayName("4. Erro: JSON Vazio deve ser capturado")
    void jsonVazio() throws JsonProcessingException {
        String json = "";

        when(objectMapper.readValue(json, TransacaoKafkaDTO.class))
                .thenThrow(new RuntimeException("MismatchedInputException"));

        assertDoesNotThrow(() -> listener.ouvir(json));
        verify(useCase, never()).execute(any());
    }

    @Test
    @DisplayName("5. Erro: UseCase falha (Banco fora do ar) deve ser capturado")
    void useCaseFalhaGeral() throws JsonProcessingException {
        String json = "{}";
        TransacaoKafkaDTO dto = criarDtoValido();

        when(objectMapper.readValue(json, TransacaoKafkaDTO.class)).thenReturn(dto);

        // Simula erro grave no processamento
        doThrow(new RuntimeException("Database Connection Timeout")).when(useCase).execute(any());

        assertDoesNotThrow(() -> listener.ouvir(json));
    }

    @Test
    @DisplayName("6. Erro: NullPointer no DTO (Dados nulos) deve ser capturado")
    void dtoNulo() throws JsonProcessingException {
        String json = "{}";
        // Mapper retorna null (simulação)
        when(objectMapper.readValue(json, TransacaoKafkaDTO.class)).thenReturn(null);

        assertDoesNotThrow(() -> listener.ouvir(json));

        // Vai dar NullPointer no dto.toDomain(), o catch deve pegar
        verify(useCase, never()).execute(any());
    }

    @Test
    @DisplayName("7. Lógica das Aspas: JSON normal NÃO deve chamar readValue(String)")
    void logicaAspasNegativa() throws JsonProcessingException {
        String json = "{\"id\": 1}"; // Não começa com aspas duplas
        TransacaoKafkaDTO dto = criarDtoValido();

        when(objectMapper.readValue(json, TransacaoKafkaDTO.class)).thenReturn(dto);

        listener.ouvir(json);

        // Verifica que NUNCA tentou converter para String primeiro
        verify(objectMapper, never()).readValue(anyString(), eq(String.class));
    }
}