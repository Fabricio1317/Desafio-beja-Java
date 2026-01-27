package com.becajava.ms_transaction_worker.infra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrasilApiDTO(
        @JsonProperty("cotacoes")
        List<CotacaoItem> cotacoes
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CotacaoItem(
            @JsonProperty("cotacao_venda")
            Double cotacaoVenda
    ) {}
    public Double ask() {
        if (cotacoes != null && !cotacoes.isEmpty()) {
            return cotacoes.get(cotacoes.size() - 1).cotacaoVenda();
        }
        return null;
    }
}