package com.becajava.ms_user.dto;



public record UsuarioRequestDTO(
        String nome,
        String cpf,
        String email,
        String senha

)  {
}
