package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway;
import com.becajava.ms_transaction_worker.infra.dto.BrasilApiDTO;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ValidadorGtwImpl implements ValidadorGateway {

    private final UsuarioClient usuarioClient;
    private final BrasilApiClient brasilApiClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ValidadorGtwImpl(UsuarioClient usuarioClient, BrasilApiClient brasilApiClient) {
        this.usuarioClient = usuarioClient;
        this.brasilApiClient = brasilApiClient;
    }

    @Override
    public boolean usuarioExiste(Long usuarioId) {
        try {
            return usuarioClient.buscarPorId(usuarioId) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public double obterCotacaoDolar() {
        LocalDate dataParaBuscar = LocalDate.now();

        for (int i = 0; i < 5; i++) {
            String dataFormatada = dataParaBuscar.format(formatter);
            try {
                System.out.println(" Tentando BrasilAPI (USD) para: " + dataFormatada);
                BrasilApiDTO response = brasilApiClient.buscarCotacao("USD", dataFormatada);

                if (response != null && response.ask() != null) {
                    double valorDolar = response.ask();
                    System.out.println("  Cotação encontrada: R$ " + valorDolar);
                    return valorDolar;
                }
            } catch (Exception e) {
                System.out.println("  Sem dados para " + dataFormatada + ". Tentando dia anterior...");
            }
            dataParaBuscar = dataParaBuscar.minusDays(1);
        }

        System.out.println(" ℹFalha após 5 tentativas. Usando fallback: 5.0");
        return 5.0;
    }
}