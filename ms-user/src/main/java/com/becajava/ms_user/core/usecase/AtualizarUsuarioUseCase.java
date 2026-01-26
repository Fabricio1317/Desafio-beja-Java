package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.domain.Usuario;
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
        // 1. Busca o usuário original (para garantir que existe e pegar dados antigos)
        Usuario usuarioAntigo = usuarioGateway.buscaPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // 2. Validação: O novo E-mail já existe em OUTRA conta?
        if (!usuarioAntigo.getEmail().equals(dto.email()) && usuarioGateway.existePorEmail(dto.email())) {
            throw new RuntimeException("E-mail já cadastrado por outro usuário.");
        }

        // 3. Validação: O novo CPF já existe em OUTRA conta?
        if (!usuarioAntigo.getCpf().equals(dto.cpf()) && usuarioGateway.existePorCpf(dto.cpf())) {
            throw new RuntimeException("CPF já cadastrado por outro usuário.");
        }

        // 4. Lógica da Senha (Segurança)
        // Se o usuário mandou senha nova -> Criptografa
        // Se mandou vazio -> Mantém a senha antiga (já criptografada)
        String senhaFinal;
        if (dto.senha() != null && !dto.senha().isBlank()) {
            senhaFinal = passwordEncoderGateway.encode(dto.senha());
        } else {
            senhaFinal = usuarioAntigo.getSenha();
        }

        // 5. Monta o objeto atualizado mantendo o ID
        Usuario usuarioAtualizado = new Usuario(
                id,
                dto.nome(),
                dto.cpf(),
                dto.email(),
                senhaFinal
        );

        // 6. Chama o Gateway para salvar
        Usuario salvo = usuarioGateway.atualizar(usuarioAtualizado);

        return new UsuarioResponseDTO(salvo);
    }
}