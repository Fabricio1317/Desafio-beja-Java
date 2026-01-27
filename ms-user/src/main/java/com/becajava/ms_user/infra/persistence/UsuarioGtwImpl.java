package com.becajava.ms_user.infra.persistence;

import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import com.becajava.ms_user.core.exception.RegraNegocioException;
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
        try {
            UsuarioEntity entity = new UsuarioEntity(
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getCpf(),
                    usuario.getEmail(),
                    usuario.getSenha()
            );

            UsuarioEntity salvo = repository.save(entity);

            return new Usuario(
                    salvo.getId(),
                    salvo.getNome(),
                    salvo.getCpf(),
                    salvo.getEmail(),
                    salvo.getSenha()
            );
        } catch (Exception e) {
            throw new RegraNegocioException("Erro ao salvar usuario no banco de dados");
        }
    }

    @Override
    public boolean existePorCpf(String cpf) {
        try {
            return repository.existsByCpf(cpf);
        } catch (Exception e) {
            throw new RegraNegocioException("Erro ao verificar CPF");
        }
    }

    @Override
    public boolean existePorEmail(String email) {
        try {
            return repository.existsByEmail(email);
        } catch (Exception e) {
            throw new RegraNegocioException("Erro ao verificar email");
        }
    }

    @Override
    public Optional<Usuario> buscaPorEmail(String email) {
        try {
            return repository.findByEmail(email).map(entity -> new Usuario(
                    entity.getId(),
                    entity.getNome(),
                    entity.getCpf(),
                    entity.getEmail(),
                    entity.getSenha()
            ));
        } catch (Exception e) {
            throw new RegraNegocioException("Erro ao buscar usuario por email");
        }
    }

    @Override
    public Optional<Usuario> buscaPorId(Long id) {
        try {
            return repository.findById(id).map(entity -> new Usuario(
                    entity.getId(),
                    entity.getNome(),
                    entity.getCpf(),
                    entity.getEmail(),
                    entity.getSenha()
            ));
        } catch (Exception e) {
            throw new RegraNegocioException("Erro ao buscar usuario por ID");
        }
    }

    @Override
    public Usuario atualizar(Usuario usuario) {
        try {
            UsuarioEntity entity = new UsuarioEntity(
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getCpf(),
                    usuario.getEmail(),
                    usuario.getSenha()
            );

            UsuarioEntity salvo = repository.save(entity);

            return new Usuario(
                    salvo.getId(),
                    salvo.getNome(),
                    salvo.getCpf(),
                    salvo.getEmail(),
                    salvo.getSenha()
            );
        } catch (Exception e) {
            throw new RegraNegocioException("Erro ao atualizar usuario");
        }
    }

    @Override
    public void deletar(Long id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new RegraNegocioException("Erro ao deletar usuario");
        }
    }
}