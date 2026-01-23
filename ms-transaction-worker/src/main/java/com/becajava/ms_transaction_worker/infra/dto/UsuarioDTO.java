    package com.becajava.ms_transaction_worker.infra.dto;

    import java.math.BigDecimal;

    public record UsuarioDTO(
            Long id,
            String nome,
            String cpf,
            String email,
            BigDecimal saldo
    ) {
    }
