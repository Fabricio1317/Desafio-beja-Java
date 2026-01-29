package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.infra.dto.BrasilApiDTO;
import com.becajava.ms_transaction_worker.infra.dto.UsuarioDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidadorGtwImplTest {

    @InjectMocks
    private ValidadorGtwImpl validadorGtw;

    @Mock private UsuarioClient usuarioClient;
    @Mock private BrasilApiClient brasilApiClient;

    // --- MÉTODOS AUXILIARES ---

    // Cria um DTO real da BrasilAPI com uma lista de cotações
    private BrasilApiDTO criarBrasilApiDto(double valorUltimaCotacao) {
        // Simula uma lista onde o último valor é o que importa (lógica do seu DTO)
        var item1 = new BrasilApiDTO.CotacaoItem(4.00); // Valor antigo irrelevante
        var item2 = new BrasilApiDTO.CotacaoItem(valorUltimaCotacao); // Valor atual

        return new BrasilApiDTO(List.of(item1, item2));
    }

    // --- TESTES: usuarioExiste ---

    @Test
    @DisplayName("1. Usuário Existe: Client retorna DTO preenchido -> Retorna TRUE")
    void usuarioExiste() {
        // Usando o DTO real que você enviou
        UsuarioDTO dto = new UsuarioDTO(1L, "12345678900", "teste@email.com", "Fulano");

        when(usuarioClient.buscarPorId(1L)).thenReturn(dto);

        assertTrue(validadorGtw.usuarioExiste(1L));
    }

    @Test
    @DisplayName("2. Usuário Não Existe: Client retorna NULL -> Retorna FALSE")
    void usuarioNulo() {
        when(usuarioClient.buscarPorId(1L)).thenReturn(null);

        assertFalse(validadorGtw.usuarioExiste(1L));
    }

    @Test
    @DisplayName("3. Erro na API de Usuário: Exception -> Retorna FALSE (Tratamento try-catch)")
    void usuarioErroApi() {
        when(usuarioClient.buscarPorId(anyLong())).thenThrow(new RuntimeException("Timeout"));

        assertFalse(validadorGtw.usuarioExiste(1L));
    }

    // --- TESTES: obterCotacaoDolar (Lógica de Retry) ---

    @Test
    @DisplayName("4. Cotação Hoje: Sucesso na primeira tentativa")
    void cotacaoHoje() {
        BrasilApiDTO response = criarBrasilApiDto(5.25);

        // Mock aceita qualquer data (para o teste não quebrar amanhã)
        when(brasilApiClient.buscarCotacao(eq("USD"), anyString())).thenReturn(response);

        double resultado = validadorGtw.obterCotacaoDolar();

        assertEquals(5.25, resultado);
        verify(brasilApiClient, times(1)).buscarCotacao(eq("USD"), anyString());
    }

    @Test
    @DisplayName("5. Cotação Retry: Falha hoje e ontem, mas acha anteontem")
    void cotacaoRetry() {
        BrasilApiDTO response = criarBrasilApiDto(5.15);

        when(brasilApiClient.buscarCotacao(eq("USD"), anyString()))
                .thenThrow(new RuntimeException("Erro Hoje"))  // 1ª tentativa: Erro
                .thenReturn(null)                              // 2ª tentativa: Null
                .thenReturn(new BrasilApiDTO(Collections.emptyList())) // 3ª tentativa: Lista vazia (ask() retorna null)
                .thenReturn(response);                         // 4ª tentativa: Sucesso!

        double resultado = validadorGtw.obterCotacaoDolar();

        assertEquals(5.15, resultado);
        verify(brasilApiClient, times(4)).buscarCotacao(eq("USD"), anyString());
    }

    @Test
    @DisplayName("6. Fallback Total: Falha 5 vezes -> Retorna 5.0")
    void cotacaoFallback() {
        // Simula erro em todas as chamadas
        when(brasilApiClient.buscarCotacao(eq("USD"), anyString()))
                .thenThrow(new RuntimeException("API Off"));

        double resultado = validadorGtw.obterCotacaoDolar();

        assertEquals(5.0, resultado);
        // Garante que tentou 5 vezes antes de desistir
        verify(brasilApiClient, times(5)).buscarCotacao(eq("USD"), anyString());
    }

    @Test
    @DisplayName("7. Fallback DTO Vazio: API retorna JSON mas sem cotações -> Retorna 5.0")
    void cotacaoSemDados() {
        // DTO válido mas com lista nula ou vazia
        BrasilApiDTO dtoVazio = new BrasilApiDTO(null);

        when(brasilApiClient.buscarCotacao(eq("USD"), anyString())).thenReturn(dtoVazio);

        double resultado = validadorGtw.obterCotacaoDolar();

        assertEquals(5.0, resultado);
        verify(brasilApiClient, times(5)).buscarCotacao(eq("USD"), anyString());
    }
}