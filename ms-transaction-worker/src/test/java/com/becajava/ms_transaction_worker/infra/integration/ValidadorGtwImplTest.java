package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.infra.dto.BrasilApiDTO;
import com.becajava.ms_transaction_worker.infra.dto.UsuarioDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidadorGtwImplTest {

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private BrasilApiClient brasilApiClient;

    @InjectMocks
    private ValidadorGtwImpl validador;

    @Test
    void deveRetornarTrueQuandoUsuarioExiste() {

        UsuarioDTO usuarioMock = new UsuarioDTO(1L, "12345678900", "teste@teste.com", "Teste");

        when(usuarioClient.buscarPorId(1L)).thenReturn(usuarioMock);

        boolean resultado = validador.usuarioExiste(1L);

        Assertions.assertTrue(resultado);
    }

    @Test
    void deveRetornarFalseQuandoUsuarioNaoExiste() {
        when(usuarioClient.buscarPorId(99L)).thenThrow(new RuntimeException());

        boolean resultado = validador.usuarioExiste(99L);

        Assertions.assertFalse(resultado);
    }

    @Test
    void deveRetornarCotacaoDePrimeira() {
        BrasilApiDTO dtoMock = mock(BrasilApiDTO.class);
        when(dtoMock.valor()).thenReturn(5.50);

        when(brasilApiClient.buscarCotacao(eq("USD"), anyString()))
                .thenReturn(List.of(dtoMock));

        Double valor = validador.obterCotacaoDolar();

        Assertions.assertEquals(5.50, valor);
        verify(brasilApiClient, times(1)).buscarCotacao(anyString(), anyString());
    }

    @Test
    void deveRetornarFallbackQuandoApiEstiverFora() {
        when(brasilApiClient.buscarCotacao(eq("USD"), anyString()))
                .thenThrow(new RuntimeException("API Error"));

        Double valor = validador.obterCotacaoDolar();

        Assertions.assertEquals(5.0, valor);
        verify(brasilApiClient, times(4)).buscarCotacao(anyString(), anyString());
    }

    @Test
    void deveTentarDiaAnteriorSeHojeFalhar() {
        BrasilApiDTO dtoMock = mock(BrasilApiDTO.class);
        when(dtoMock.valor()).thenReturn(6.00);

        when(brasilApiClient.buscarCotacao(eq("USD"), anyString()))
                .thenThrow(new RuntimeException("Erro hoje"))
                .thenReturn(List.of(dtoMock));

        Double valor = validador.obterCotacaoDolar();

        Assertions.assertEquals(6.00, valor);
        verify(brasilApiClient, times(2)).buscarCotacao(anyString(), anyString());
    }
}