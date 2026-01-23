package com.becajava.ms_transaction_worker.infra.config;

import com.becajava.ms_transaction_worker.core.gateway.TransacaoGateway;
import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway;
import com.becajava.ms_transaction_worker.core.usecase.ProcessarTransacaoUseCase;
import com.becajava.ms_transaction_worker.infra.integration.MockApiFinanceiroClient;
import com.becajava.ms_transaction_worker.infra.integration.UsuarioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {
    @Bean
    public ProcessarTransacaoUseCase processarTransacaoUseCase(TransacaoGateway transacaoGateway, ValidadorGateway validadorGateway, UsuarioClient usuarioClient, MockApiFinanceiroClient mockApiFinanceiroClient){
        return new ProcessarTransacaoUseCase(transacaoGateway,validadorGateway, usuarioClient, mockApiFinanceiroClient);
    }
}
