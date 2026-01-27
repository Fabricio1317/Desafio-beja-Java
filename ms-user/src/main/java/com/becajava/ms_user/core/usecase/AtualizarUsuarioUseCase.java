package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.exception.RegraNegocioException;
import com.becajava.ms_user.core.gateway.PasswordEncoderGateway;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import com.becajava.ms_user.dto.UsuarioRequestDTO;
import com.becajava.ms_user.dto.UsuarioResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class AtualizarUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoderGateway passwordEncoderGateway;

    public AtualizarUsuarioUseCase(UsuarioGateway usuarioGateway, PasswordEncoderGateway passwordEncoderGateway) {
        this.usuarioGateway = usuarioGateway;
        this.passwordEncoderGateway = passwordEncoderGateway;
    }

    public UsuarioResponseDTO execute(Long id, UsuarioRequestDTO dto) {
        Usuario usuarioAntigo = usuarioGateway.buscaPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado."));

        if (!usuarioAntigo.getEmail().equals(dto.email()) && usuarioGateway.existePorEmail(dto.email())) {
            throw new RegraNegocioException("E-mail ja cadastrado por outro usuario.");
        }

        if (!usuarioAntigo.getCpf().equals(dto.cpf()) && usuarioGateway.existePorCpf(dto.cpf())) {
            throw new RegraNegocioException("CPF ja cadastrado por outro usuario.");
        }

        String senhaFinal;
        if (dto.senha() != null && !dto.senha().isBlank()) {
            senhaFinal = passwordEncoderGateway.encode(dto.senha());
        } else {
            senhaFinal = usuarioAntigo.getSenha();
        }

        Usuario usuarioAtualizado = new Usuario(
                id,
                dto.nome(),
                dto.cpf(),
                dto.email(),
                senhaFinal
        );

        Usuario salvo = usuarioGateway.atualizar(usuarioAtualizado);

        return new UsuarioResponseDTO(salvo);
    }
}