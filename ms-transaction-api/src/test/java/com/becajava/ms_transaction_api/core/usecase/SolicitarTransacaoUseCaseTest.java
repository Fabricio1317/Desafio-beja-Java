package com.becajava.ms_transaction_api.core.usecase;// JUnit 5
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

// Mockito
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;


// Tipos de Dados e Java
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;

// Classes do seu Projeto (Ajuste conforme seus pacotes)
import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.dto.TransacaoRequestDTO;
import com.becajava.ms_transaction_api.core.exception.RegraDeNegocioException;
import com.becajava.ms_transaction_api.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_api.core.gateway.MensageriaGateway;

@ExtendWith(MockitoExtension.class)
class SolicitarTransacaoUseCaseTest {

    @Mock
    private MensageriaGateway mensageriaGateway;
    @Mock
    private TransacaoGateway transacaoGateway;

    @InjectMocks
    private SolicitarTransacaoUseCase useCase;

    @Test
    @DisplayName("Deve salvar e enviar transação quando dados são válidos")
    void deveSolicitarComSucesso() {
        var dto = new TransacaoRequestDTO(1L, new BigDecimal("100.00"), "RECEITA", "SALARIO", "Teste");
        var transacaoSalva = new Transacao();
        transacaoSalva.setId(UUID.randomUUID());

        when(transacaoGateway.salvar(any(Transacao.class))).thenReturn(transacaoSalva);


        useCase.execute(dto);


        verify(transacaoGateway, times(1)).salvar(any());
        verify(mensageriaGateway, times(1)).enviar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor for zero ou negativo")
    void deveFalharValorInvalido() {
        var dtoZero = new TransacaoRequestDTO(1L, BigDecimal.ZERO, "RECEITA", "X", "Y");

        assertThrows(RegraDeNegocioException.class, () -> useCase.execute(dtoZero));
    }
}