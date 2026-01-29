package com.becajava.ms_transaction_api.core.usecase;

import com.becajava.ms_transaction_api.core.domain.StatusTransacao;
import com.becajava.ms_transaction_api.infra.dto.BalancoPeriodoDTO;
import com.becajava.ms_transaction_api.infra.persistence.TransacaoEntity;
import com.becajava.ms_transaction_api.infra.persistence.TransacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GerarRelatorioUseCaseTest {

    @Mock
    private TransacaoRepository repository;

    @InjectMocks
    private GerarRelatorioUseCase useCase;

    @Test
    @DisplayName("Deve calcular saldo corretamente usando os campos do record")
    void deveGerarRelatorioComCalculosCorretos() {

        Long usuarioId = 1L;
        TransacaoEntity t1 = new TransacaoEntity(UUID.randomUUID(), usuarioId, StatusTransacao.PENDENTE,
                new BigDecimal("1000.00"), "RECEITA", "SALARIO", "Salário");

        TransacaoEntity t2 = new TransacaoEntity(UUID.randomUUID(), usuarioId, StatusTransacao.PENDENTE,
                new BigDecimal("300.00"), "DESPESA", "ALIMENTACAO", "Almoço");

        when(repository.findByUsuarioIdAndDataCriacaoBetween(anyLong(), any(), any()))
                .thenReturn(List.of(t1, t2));

        Map<String, Object> resultado = useCase.execute(usuarioId, LocalDateTime.now(), LocalDateTime.now());


        BalancoPeriodoDTO balanco = (BalancoPeriodoDTO) resultado.get("balanco");

        assertEquals(new BigDecimal("1000.00"), balanco.totalReceitas());
        assertEquals(new BigDecimal("300.00"), balanco.totalDespesas());
        assertEquals(new BigDecimal("700.00"), balanco.saldoPeriodo());
    }
}