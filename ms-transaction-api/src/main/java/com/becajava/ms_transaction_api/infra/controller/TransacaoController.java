package com.becajava.ms_transaction_api.infra.controller;

import com.becajava.ms_transaction_api.core.usecase.BuscarTransacaoUseCase;
import com.becajava.ms_transaction_api.core.usecase.SolicitarTransacaoUseCase;
import com.becajava.ms_transaction_api.dto.TransacaoRequestDTO;
import com.becajava.ms_transaction_api.dto.TransacaoRespondeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/{transacoes}")
public class TransacaoController {

    private final SolicitarTransacaoUseCase solicitarTransacaoUseCase;
    private final BuscarTransacaoUseCase buscarTransacaoUseCase;

    public TransacaoController(SolicitarTransacaoUseCase solicitarTransacaoUseCase, BuscarTransacaoUseCase buscarTransacaoUseCase) {
        this.solicitarTransacaoUseCase = solicitarTransacaoUseCase;
        this.buscarTransacaoUseCase = buscarTransacaoUseCase;
    }

    @PostMapping
    public ResponseEntity<TransacaoRespondeDTO> criar(@RequestBody TransacaoRequestDTO dto){
        TransacaoRespondeDTO resposta = solicitarTransacaoUseCase.execute(dto);
        return ResponseEntity.accepted().body(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoRespondeDTO> buscar(@PathVariable UUID id){
        TransacaoRespondeDTO resposta = buscarTransacaoUseCase.execute(id);
        return ResponseEntity.ok(resposta);
    }
}
