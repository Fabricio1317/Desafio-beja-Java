package com.becajava.ms_user.dto;


import com.becajava.ms_user.core.domain.UserRole;

public record UsuarioRequestDTO(
        String nome,
        String cpf,
        String email,
        String senha,
        UserRole role

)  {
}
