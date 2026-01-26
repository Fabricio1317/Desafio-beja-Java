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
        // Loop para tentar hoje, ontem, anteontem... (caso seja fim de semana)
        for (int i = 0; i < 4; i++) {
            try {
                // Formata a data: 2026-01-25
                String data = LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                // Busca a lista na API
                List<BrasilApiDTO> cotacoes = brasilApiClient.buscarCotacao("USD", data);

                // Se tiver dados, retorna o valor e ENCERRA o método
                if (cotacoes != null && !cotacoes.isEmpty()) {
                    return cotacoes.get(cotacoes.size() - 1).valor();
                }
            } catch (Exception e) {
                // Se der erro (404/Timeout), ignora e o loop roda de novo para o dia anterior
                continue;
            }
        }
            System.out.println("⚠️ API Indisponível ou Feriado prolongado. Usando fallback 5.0");
            return 5.0;

        }
    }

