package com.becajava.ms_transaction_api.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransacaoRequestDTO(
        @NotNull(message = "ID do usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "Valor é obrigatório")
        BigDecimal valor,

        @NotBlank(message = "Tipo é obrigatório (RECEITA ou DESPESA)")
        String tipo,

        @NotBlank(message = "Categoria é obrigatória")
        String categoria,

        @NotBlank(message = "Descrição é obrigatória")
        String descricao
) {}