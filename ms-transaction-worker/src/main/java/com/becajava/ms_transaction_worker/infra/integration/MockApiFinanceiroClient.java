package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.infra.dto.ContaExternaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mock-financeiro", url = "https://6972550232c6bacb12c69d47.mockapi.io")
public interface MockApiFinanceiroClient {

    @GetMapping("/carteiras")
    List<ContaExternaDTO> buscarCarteiraPorUserId(@RequestParam("userId") Long userId);

    @PutMapping("/carteiras/{id}")
    void atualizarSaldo(@PathVariable("id") String id, @RequestBody ContaExternaDTO dto);
}