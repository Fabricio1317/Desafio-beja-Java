package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.core.gateway.ValidadorGateway;
import com.becajava.ms_transaction_worker.infra.dto.BrasilApiDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


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

        for (int i = 0; i < 4; i++) {
            try {

                String data = LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                List<BrasilApiDTO> cotacoes = brasilApiClient.buscarCotacao("USD", data);


                if (cotacoes != null && !cotacoes.isEmpty()) {
                    return cotacoes.get(cotacoes.size() - 1).valor();
                }
            } catch (Exception e) {

            }
        }
            System.out.println("API Indisponível ou Feriado prolongado. Usando fallback 5.0");
            return 5.0;

        }
    }

