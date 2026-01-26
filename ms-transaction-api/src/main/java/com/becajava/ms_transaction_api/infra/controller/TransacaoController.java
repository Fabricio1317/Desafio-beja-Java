package com.becajava.ms_transaction_api.infra.controller;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.usecase.BuscarTransacaoUseCase;
import com.becajava.ms_transaction_api.core.usecase.GerarExtratoPdfUseCase;
import com.becajava.ms_transaction_api.core.usecase.SolicitarTransacaoUseCase;
import com.becajava.ms_transaction_api.dto.TransacaoRequestDTO;
import com.becajava.ms_transaction_api.dto.TransacaoRespondeDTO;
import com.becajava.ms_transaction_api.infra.persistence.TransacaoEntity;
import com.becajava.ms_transaction_api.infra.persistence.TransacaoRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final SolicitarTransacaoUseCase solicitarTransacaoUseCase;
    private final BuscarTransacaoUseCase buscarTransacaoUseCase;

    // Dependências para o PDF
    private final TransacaoRepository transacaoRepository;
    private final GerarExtratoPdfUseCase gerarExtratoPdfUseCase;

    // Construtor
    public TransacaoController(SolicitarTransacaoUseCase solicitarTransacaoUseCase,
                               BuscarTransacaoUseCase buscarTransacaoUseCase,
                               TransacaoRepository transacaoRepository,
                               GerarExtratoPdfUseCase gerarExtratoPdfUseCase) {
        this.solicitarTransacaoUseCase = solicitarTransacaoUseCase;
        this.buscarTransacaoUseCase = buscarTransacaoUseCase;
        this.transacaoRepository = transacaoRepository;
        this.gerarExtratoPdfUseCase = gerarExtratoPdfUseCase;
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

    // 👇 ENDPOINT DO PDF CORRIGIDO
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarPdf() {
        List<TransacaoEntity> entities = transacaoRepository.findAll();


        List<Transacao> transacoesDomain = entities.stream()
                .map(entity -> {
                    Transacao t = new Transacao();
                    t.setId(entity.getId());
                    t.setPagadorId(entity.getPagadorId());
                    t.setRecebedorId(entity.getRecebedorId());
                    t.setValor(entity.getValor());
                    t.setTipo(entity.getTipo());
                    t.setStatus(entity.getStatus());
                    t.setDataCriacao(entity.getDataCriacao());
                    return t;
                })
                .collect(Collectors.toList());


        byte[] pdfBytes = gerarExtratoPdfUseCase.gerar(transacoesDomain);


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=extrato-beca.pdf")
                .body(pdfBytes);
    }
}