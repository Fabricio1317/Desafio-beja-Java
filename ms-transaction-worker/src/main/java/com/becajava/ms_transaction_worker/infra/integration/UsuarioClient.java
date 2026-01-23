package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.infra.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "ms-user", url = "http://localhost:8081/users")
public interface UsuarioClient {

    @GetMapping("/{id}")
    UsuarioDTO buscarPorId(@PathVariable("id") Long id);


    @PutMapping("/{id}/atualizar-saldo")
    void atualizarSaldo(@PathVariable("id") Long id, @RequestParam("novoSaldo") BigDecimal novoSaldo);
}
