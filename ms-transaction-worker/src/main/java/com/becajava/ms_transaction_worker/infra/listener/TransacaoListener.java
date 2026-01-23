package com.becajava.ms_transaction_worker.infra.listener;

import com.becajava.ms_transaction_worker.core.usecase.ProcessarTransacaoUseCase;
import com.becajava.ms_transaction_worker.infra.dto.TransacaoKafkaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component; // <--- IMPORTANTE: Faltava importar isso

@Component // <--- OBRIGATÓRIO: Sem isso o Kafka não liga!
public class TransacaoListener {

    private final ProcessarTransacaoUseCase processarTransacaoUseCase;
    private final ObjectMapper objectMapper;

    public TransacaoListener(ProcessarTransacaoUseCase processarTransacaoUseCase, ObjectMapper objectMapper) {
        this.processarTransacaoUseCase = processarTransacaoUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transaction.requested", groupId = "${spring.kafka.consumer.group-id}")
    public void ouvir(String mensagemJson){
        try {
            System.out.println("Recebi: " + mensagemJson); // Log para ver chegando

            TransacaoKafkaDTO dto = objectMapper.readValue(mensagemJson, TransacaoKafkaDTO.class);
            processarTransacaoUseCase.execute(dto.toDomain());

            System.out.println("Processado com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro fatal no listener: "+ e.getMessage());
            e.printStackTrace(); // Ajuda a ver onde quebrou
        }
    }
}