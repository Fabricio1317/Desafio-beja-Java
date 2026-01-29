package com.becajava.ms_transaction_api.infra.controller;

import com.becajava.ms_transaction_api.core.usecase.GerarRelatorioUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final GerarRelatorioUseCase gerarRelatorioUseCase;

    public RelatorioController(GerarRelatorioUseCase gerarRelatorioUseCase) {
        this.gerarRelatorioUseCase = gerarRelatorioUseCase;
    }

    @GetMapping("/analise/{usuarioId}")
    public ResponseEntity<Map<String, Object>> obterAnaliseMensal(


            @PathVariable Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime fim
    ) {
        if (inicio == null) inicio = LocalDateTime.now().withDayOfMonth(1);
        if (fim == null) fim = LocalDateTime.now();

        Map<String, Object> resultado = gerarRelatorioUseCase.execute(usuarioId, inicio, fim);

        return ResponseEntity.ok(resultado);
    }
}