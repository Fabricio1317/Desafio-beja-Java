package com.becajava.ms_transaction_api.core.usecase;

import com.becajava.ms_transaction_api.infra.dto.BalancoPeriodoDTO;
import com.becajava.ms_transaction_api.infra.dto.ResumoCategoriaDTO;
import com.becajava.ms_transaction_api.infra.persistence.TransacaoEntity;
import com.becajava.ms_transaction_api.infra.persistence.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GerarRelatorioUseCase {

    private final TransacaoRepository repository;

    public GerarRelatorioUseCase(TransacaoRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> execute(Long usuarioId, LocalDateTime inicio, LocalDateTime fim) {

        LocalDateTime dataInicio = inicio.with(LocalTime.MIN);

        LocalDateTime dataFim = fim.with(LocalTime.MAX);

        System.out.println(" Buscando transações entre: " + dataInicio + " e " + dataFim);

        List<TransacaoEntity> transacoes = repository.findByUsuarioIdAndDataCriacaoBetween(usuarioId, dataInicio, dataFim);


        BigDecimal totalReceitas = transacoes.stream()
                .filter(t -> "RECEITA".equalsIgnoreCase(t.getTipo()))
                .map(TransacaoEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacoes.stream()
                .filter(t -> "DESPESA".equalsIgnoreCase(t.getTipo()))
                .map(TransacaoEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);
        BalancoPeriodoDTO balanco = new BalancoPeriodoDTO(totalReceitas, totalDespesas, saldo);

        Map<String, BigDecimal> mapaCategorias = transacoes.stream()
                .filter(t -> "DESPESA".equalsIgnoreCase(t.getTipo()))
                .collect(Collectors.groupingBy(
                        t -> t.getCategoria() == null ? "OUTROS" : t.getCategoria(),
                        Collectors.reducing(BigDecimal.ZERO, TransacaoEntity::getValor, BigDecimal::add)
                ));

        List<ResumoCategoriaDTO> listaCategorias = mapaCategorias.entrySet().stream()
                .map(entry -> new ResumoCategoriaDTO(entry.getKey(), entry.getValue()))
                .toList();

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("periodo", Map.of("inicio", dataInicio, "fim", dataFim));
        relatorio.put("balanco", balanco);
        relatorio.put("detalheCategorias", listaCategorias);

        return relatorio;
    }
}