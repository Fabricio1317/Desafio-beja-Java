package com.becajava.ms_user.core.gateway;

import com.becajava.ms_user.core.domain.Usuario;

import java.util.Optional;

public interface UsuarioGateway {
    Usuario criarUsuario(Usuario usuario);
    boolean existePorCpf(String cpf);
    boolean existePorEmail(String email);
    Optional<Usuario> buscaPorEmail(String Email);
    Optional<Usuario> buscaPorId(Long id);
    Usuario atualizar(Usuario usuario);
    void deletar(Long id);
}