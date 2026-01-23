package com.becajava.ms_user.core.gateway;

import com.becajava.ms_user.core.domain.Usuario;

public interface TokenGateway {
    String gerarToken(Usuario usuario);
}
