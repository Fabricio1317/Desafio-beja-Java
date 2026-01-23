package com.becajava.ms_transaction_api.infra.service;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.gateway.MensageriaGateway;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService implements MensageriaGateway {
   private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }



    @Override
    public void enviarParaFila(Transacao transacao) {
        kafkaTemplate.send("transaction.requested", transacao);
    }
}
