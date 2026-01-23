package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway;
import com.becajava.ms_transaction_worker.infra.dto.BrasilApiDTO;
import org.springframework.stereotype.Service;


@Service
public class ValidadorGtwImpl implements ValidadorGateway {

    private final UsuarioClient usuarioClient;
    private final BrasilApiClient brasilApiClient;


    public ValidadorGtwImpl(UsuarioClient usuarioClient, BrasilApiClient brasilApiClient) {
        this.usuarioClient = usuarioClient;
        this.brasilApiClient = brasilApiClient;

    }


    @Override
    public boolean usuarioExiste(Long id) {
        try {
            return usuarioClient.buscarPorId(id) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Double obterCotacaoDolar() {
        try {
            // Chamando a BrasilAPI para a moeda USD
            BrasilApiDTO resposta = brasilApiClient.buscarCotacao("USD");

            if (resposta != null && resposta.valor() != null) {
                System.out.println("🇧🇷 [BrasilAPI] Cotação do Dólar: " + resposta.valor());
                return resposta.valor();
            }
            return 5.0;
        } catch (Exception e) {
            System.err.println("⚠️ Falha na BrasilAPI: " + e.getMessage());
            return 5.0; // Fallback de segurança
        }
    }
}
