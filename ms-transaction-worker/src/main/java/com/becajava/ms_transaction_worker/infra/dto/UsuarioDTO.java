package com.becajava.ms_transaction_worker.infra.dto;

public record UsuarioDTO(
        Long id,
        String cpf,
        String email,
        String nome
) {}