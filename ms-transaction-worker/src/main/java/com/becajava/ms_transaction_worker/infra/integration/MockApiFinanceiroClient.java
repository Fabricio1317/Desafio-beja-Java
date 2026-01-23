package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.infra.dto.ContaExternaDTO;
import com.becajava.ms_transaction_worker.infra.dto.TransacaoMockDTO;
import com.becajava.ms_transaction_worker.infra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(
        name = "financeiro-mock",
        url = "https://6972550232c6bacb12c69d47.mockapi.io",
        configuration = FeignConfig.class
)
public interface MockApiFinanceiroClient {

    @PostMapping("/historico")
    void salvarNoMock(@RequestBody TransacaoMockDTO dto);

    @GetMapping("/contas/{id}")
        ContaExternaDTO buscarConta(@PathVariable("id") Long id);

    @PutMapping("/contas/{id}")
    void atualizarSaldoExterno(@PathVariable("id") Long id, @RequestBody ContaExternaDTO dto);

}