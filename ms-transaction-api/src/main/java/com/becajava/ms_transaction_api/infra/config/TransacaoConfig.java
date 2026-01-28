package com.becajava.ms_transaction_api.infra.config;

import com.becajava.ms_transaction_api.core.gateway.MensageriaGateway;
import com.becajava.ms_transaction_api.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_api.core.usecase.BuscarTransacaoUseCase;
import com.becajava.ms_transaction_api.core.usecase.SolicitarTransacaoUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransacaoConfig {


    @Bean
    public SolicitarTransacaoUseCase solicitarTransacaoUseCase(MensageriaGateway mensageriaGateway, TransacaoGateway transacaoGateway){
        return new SolicitarTransacaoUseCase(mensageriaGateway, transacaoGateway);
    }


    @Bean
    public BuscarTransacaoUseCase buscarTransacaoUseCase(TransacaoGateway transacaoGateway){
        return new BuscarTransacaoUseCase(transacaoGateway);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}