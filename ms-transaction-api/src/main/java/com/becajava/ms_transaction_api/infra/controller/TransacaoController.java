package com.becajava.ms_transaction_api.infra.controller;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.dto.TransacaoRequestDTO;
import com.becajava.ms_transaction_api.core.usecase.BuscarTransacaoUseCase;
import com.becajava.ms_transaction_api.core.usecase.GerarExtratoPdfUseCase;
import com.becajava.ms_transaction_api.core.usecase.SolicitarTransacaoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final SolicitarTransacaoUseCase solicitarTransacaoUseCase;
    private final BuscarTransacaoUseCase buscarTransacaoUseCase;
    private final GerarExtratoPdfUseCase gerarExtratoPdfUseCase;

    public TransacaoController(
            SolicitarTransacaoUseCase solicitarTransacaoUseCase,
            BuscarTransacaoUseCase buscarTransacaoUseCase,
            GerarExtratoPdfUseCase gerarExtratoPdfUseCase
    ) {
        this.solicitarTransacaoUseCase = solicitarTransacaoUseCase;
        this.buscarTransacaoUseCase = buscarTransacaoUseCase;
        this.gerarExtratoPdfUseCase = gerarExtratoPdfUseCase;
    }

    @PostMapping
    public ResponseEntity<String> criar(@RequestBody @Valid TransacaoRequestDTO request) {
        solicitarTransacaoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Transação enviada para processamento!");
    }

    @GetMapping("/exportar/{usuarioId}")
    public ResponseEntity<byte[]> baixarPdf(@PathVariable Long usuarioId) {
        List<Transacao> transacoes = buscarTransacaoUseCase.buscarTodasPorUsuario(usuarioId);
        byte[] pdfBytes = gerarExtratoPdfUseCase.gerar(transacoes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=extrato_usuario_" + usuarioId + ".pdf")
                .body(pdfBytes);
    }
}