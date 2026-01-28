package com.becajava.ms_transaction_worker.infra.listener;

import com.becajava.ms_transaction_worker.core.usecase.ProcessarTransacaoUseCase;
import com.becajava.ms_transaction_worker.infra.dto.TransacaoKafkaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransacaoListener {

    private final ProcessarTransacaoUseCase processarTransacaoUseCase;
    private final ObjectMapper objectMapper;

    public TransacaoListener(ProcessarTransacaoUseCase processarTransacaoUseCase, ObjectMapper objectMapper) {
        this.processarTransacaoUseCase = processarTransacaoUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transaction.requested", groupId = "worker-producao-limpo-v1")
    public void ouvir(String mensagemJson) {
        try {
            Thread.sleep(500);
            System.out.println(" Mensagem recebida: " + mensagemJson);

            if (mensagemJson.startsWith("\"")) {
                mensagemJson = objectMapper.readValue(mensagemJson, String.class);
            }

            TransacaoKafkaDTO dto = objectMapper.readValue(mensagemJson, TransacaoKafkaDTO.class);
            processarTransacaoUseCase.execute(dto.toDomain());

        } catch (Exception e) {
            System.err.println("Erro ao processar: " + e.getMessage());
        }
    }
}