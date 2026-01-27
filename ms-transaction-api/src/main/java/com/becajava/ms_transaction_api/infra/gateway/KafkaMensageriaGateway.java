package com.becajava.ms_transaction_api.infra.gateway;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.gateway.MensageriaGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaMensageriaGateway implements MensageriaGateway {

    private static final Logger log = LoggerFactory.getLogger(KafkaMensageriaGateway.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaMensageriaGateway(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void enviar(Transacao transacao) {
        try {
            TransactionMessage message = new TransactionMessage(
                    transacao.getId(),
                    transacao.getUsuarioId(),
                    transacao.getValor(),
                    transacao.getTipo(),
                    transacao.getCategoria(),
                    transacao.getDescricao()
            );

            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send("transaction.requested", json);

            log.info(" Enviado para Kafka com sucesso (ID: {})", transacao.getId());

        } catch (Exception e) {
            log.error(" FALHA ao enviar mensagem para o Kafka (ID: {}). Erro: {}", transacao.getId(), e.getMessage());
            // Lançamos RuntimeException para que o Controller saiba que deu erro 500
            throw new RuntimeException("Erro de comunicação com o sistema de mensageria.", e);
        }
    }

    // Mantive o seu record interno
    private record TransactionMessage(
            UUID id,
            Long usuarioId,
            java.math.BigDecimal valor,
            String tipo,
            String categoria,
            String descricao
    ) {}
}