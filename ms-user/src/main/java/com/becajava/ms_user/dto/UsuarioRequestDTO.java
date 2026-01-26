package com.becajava.ms_user.dto;

import java.math.BigDecimal;

public record UsuarioRequestDTO(
        String nome,
        String cpf,
        String email,
        String senha

)  {
}
