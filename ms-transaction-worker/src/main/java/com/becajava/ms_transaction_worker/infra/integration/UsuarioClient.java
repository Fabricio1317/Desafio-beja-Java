package com.becajava.ms_transaction_worker.infra.integration;

import com.becajava.ms_transaction_worker.infra.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-user", url = "http://localhost:8081")
public interface UsuarioClient {
    @GetMapping("/users/{id}")
    UsuarioDTO buscarPorId(@PathVariable("id") Long id);
}