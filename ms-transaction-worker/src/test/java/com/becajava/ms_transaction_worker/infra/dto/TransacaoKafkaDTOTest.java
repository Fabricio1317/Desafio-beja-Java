package com.becajava.ms_transaction_worker.infra.dto;

import com.becajava.ms_transaction_worker.core.domain.StatusTransacao;
import com.becajava.ms_transaction_worker.core.domain.Transacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransacaoKafkaDTOTest {

    @Test
    @DisplayName("toDomain() deve converter todos os campos e definir status PENDENTE")
    void toDomain() {
        UUID id = UUID.randomUUID();
        TransacaoKafkaDTO dto = new TransacaoKafkaDTO(
                id,
                10L,
                BigDecimal.valueOf(50.0),
                "DESPESA",
                "ALIMENTACAO",
                "Almoço"
        );

        Transacao domain = dto.toDomain();

        assertNotNull(domain);
        assertEquals(id, domain.getId());
        assertEquals(10L, domain.getUsuarioId());
        assertEquals(BigDecimal.valueOf(50.0), domain.getValor());
        assertEquals("DESPESA", domain.getTipo());
        assertEquals("ALIMENTACAO", domain.getCategoria());
        assertEquals("Almoço", domain.getDescricao());

        // Verificação CRÍTICA: O DTO força o status inicial como PENDENTE
        assertEquals(StatusTransacao.PENDENTE, domain.getStatus());
    }
}