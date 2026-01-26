package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.infra.dto.BrasilApiDTO;
import com.becajava.ms_transaction_worker.infra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List; // Importante!

@FeignClient(name = "brasil-api", url = "https://brasilapi.com.br/api/cambio/v1/cotacao", configuration = FeignConfig.class)
public interface BrasilApiClient {

    // Retorna uma LISTA, pois pode ter várias cotações no mesmo dia
    @GetMapping("/{moeda}/{data}")
    List<BrasilApiDTO> buscarCotacao(@PathVariable("moeda") String moeda, @PathVariable("data") String data);
}