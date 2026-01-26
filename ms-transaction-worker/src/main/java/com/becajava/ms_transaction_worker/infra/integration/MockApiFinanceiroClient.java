package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.infra.dto.ContaExternaDTO;
import com.becajava.ms_transaction_worker.infra.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(
        name = "financeiro-mock",
        url = "https://6972550232c6bacb12c69d47.mockapi.io", // Dica: Use a variável do application.properties
        configuration = FeignConfig.class
)
public interface MockApiFinanceiroClient {


    @GetMapping("/contas")
    List<ContaExternaDTO> buscarCarteiraPorUserId(@RequestParam("userId") Long userId);


    @PutMapping("/contas/{id}")
    void atualizarSaldo(@PathVariable("id") String id, @RequestBody ContaExternaDTO dto);
}