package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.infra.dto.BrasilApiDTO;
import com.becajava.ms_transaction_worker.infra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "brasil-api", url = "https://brasilapi.com.br/api/cambio/v1", configuration = FeignConfig.class)
public interface BrasilApiClient {

    // Busca a cotação de uma moeda específica (ex: USD)
    @GetMapping("/{codigoMoeda}")
    BrasilApiDTO buscarCotacao(@PathVariable("codigoMoeda") String codigoMoeda);
}