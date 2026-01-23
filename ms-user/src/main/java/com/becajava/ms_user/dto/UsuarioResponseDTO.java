package com.becajava.ms_user.dto;

import com.becajava.ms_user.core.domain.Usuario;

import java.math.BigDecimal;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        BigDecimal saldo
) {
    public UsuarioResponseDTO(Usuario usuario){
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getSaldo());
    }
}
