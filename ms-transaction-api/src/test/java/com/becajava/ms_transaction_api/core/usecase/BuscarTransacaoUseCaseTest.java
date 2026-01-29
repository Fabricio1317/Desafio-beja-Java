package com.becajava.ms_transaction_api.core.usecase;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.dto.TransacaoRespondeDTO;
import com.becajava.ms_transaction_api.core.exception.RegraDeNegocioException;
import com.becajava.ms_transaction_api.core.gateway.TransacaoGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarTransacaoUseCaseTest {

    @Mock
    private TransacaoGateway transacaoGateway;

    @InjectMocks
    private BuscarTransacaoUseCase useCase;

    @Test
    @DisplayName("Deve retornar transação por ID existente")
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        Transacao transacao = new Transacao();
        transacao.setId(id);

        when(transacaoGateway.buscaPorId(id)).thenReturn(Optional.of(transacao));

        TransacaoRespondeDTO resultado = useCase.execute(id);

        assertNotNull(resultado);
        verify(transacaoGateway).buscaPorId(id);
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar todas por usuário sem registros")
    void deveFalharUsuarioSemRegistros() {
        when(transacaoGateway.buscarPorUsuarioId(1L)).thenReturn(Collections.emptyList());

        assertThrows(RegraDeNegocioException.class, () -> useCase.buscarTodasPorUsuario(1L));
    }
}