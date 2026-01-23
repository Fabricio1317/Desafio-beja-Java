package com.becajava.ms_user.infra.persistence;

import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioGtwImpl implements UsuarioGateway {
    private final UsuarioRepository repository;

    public UsuarioGtwImpl(UsuarioRepository repository) {
        this.repository = repository;
    }


    @Override
    public Usuario criarUsuario(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getSaldo()
        );

        UsuarioEntity salvo = repository.save(entity);

        return new Usuario(salvo.getId(),salvo.getNome(), salvo.getCpf(), salvo.getEmail(), salvo.getSenha(),salvo.getSaldo());


    }

    @Override
    public boolean existePorCpf(String cpf) {
        return repository.existsByCpf(cpf);
    }

    @Override
    public boolean existePorEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<Usuario> buscaPorEmail(String email) {
        return repository.findByEmail(email).map(entity-> new Usuario(entity.getId(), entity.getNome(), entity.getCpf(), entity.getEmail(), entity.getSenha(), entity.getSaldo()));
    }

    @Override
    public Optional<Usuario> buscaPorId(Long id) {
        return repository.findById(id).map(entity-> new Usuario(
                entity.getId(),
                entity.getNome(),
                entity.getCpf(),
                entity.getEmail(),
                entity.getSenha(),
                entity.getSaldo()
        ));
    }

    @Override
    public Usuario atualizar(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getSaldo()
        );

        UsuarioEntity salvo = repository.save(entity);

        return new Usuario(salvo.getId(),salvo.getNome(), salvo.getCpf(), salvo.getEmail(), salvo.getSenha(),salvo.getSaldo());
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
