package com.becajava.ms_user.dto;

import com.becajava.ms_user.core.domain.Usuario;

import java.math.BigDecimal;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String cpf,
        String email
) {
    public UsuarioResponseDTO(Usuario usuario){
        this(usuario.getId(), usuario.getCpf(), usuario.getNome(), usuario.getEmail());
    }
}
