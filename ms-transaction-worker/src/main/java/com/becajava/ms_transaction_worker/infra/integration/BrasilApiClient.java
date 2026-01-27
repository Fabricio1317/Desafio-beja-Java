package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.infra.dto.BrasilApiDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "brasil-api", url = "https://brasilapi.com.br/api")
public interface BrasilApiClient {

    @GetMapping("/cambio/v1/cotacao/{moeda}/{data}")
    BrasilApiDTO buscarCotacao(
            @PathVariable("moeda") String moeda,
            @PathVariable("data") String data
    );
}