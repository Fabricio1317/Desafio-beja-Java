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

    // --- MUDANÇA 1: BUSCA POR FILTRO ---
    // A MockAPI retorna uma LISTA quando usamos filtro (?userId=1)
    // Rota final será: GET /contas?userId=10
    @GetMapping("/contas")
    List<ContaExternaDTO> buscarCarteiraPorUserId(@RequestParam("userId") Long userId);

    // --- MUDANÇA 2: ATUALIZAÇÃO PELO ID DA CONTA ---
    // Depois de achar a conta acima, pegamos o ID dela e atualizamos
    // Rota final será: PUT /contas/{id_da_conta}
    @PutMapping("/contas/{id}")
    void atualizarSaldo(@PathVariable("id") String id, @RequestBody ContaExternaDTO dto);
}