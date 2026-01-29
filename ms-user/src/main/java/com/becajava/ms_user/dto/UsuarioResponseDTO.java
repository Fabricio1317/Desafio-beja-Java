package com.becajava.ms_user.dto;

import com.becajava.ms_user.core.domain.UserRole;
import com.becajava.ms_user.core.domain.Usuario;

import java.math.BigDecimal;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        UserRole role
) {
    public UsuarioResponseDTO(Usuario usuario){
        this(usuario.getId(), usuario.getNome(), usuario.getCpf(), usuario.getEmail(), usuario.getRole());
    }
}
