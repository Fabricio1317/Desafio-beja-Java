package com.becajava.ms_transaction_api.infra.config;

import com.becajava.ms_transaction_api.core.gateway.MensageriaGateway;
import com.becajava.ms_transaction_api.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_api.core.usecase.BuscarTransacaoUseCase;
import com.becajava.ms_transaction_api.core.usecase.SolicitarTransacaoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransacaoConfig {

    @Bean
    public SolicitarTransacaoUseCase solicitarTransacaoUseCase(TransacaoGateway transacaoGateway, MensageriaGateway mensageriaGateway){
        return new SolicitarTransacaoUseCase(transacaoGateway, mensageriaGateway);
    }

    @Bean
    public BuscarTransacaoUseCase buscarTransacaoUseCase(TransacaoGateway transacaoGateway){
        return new BuscarTransacaoUseCase(transacaoGateway);
    }


}
