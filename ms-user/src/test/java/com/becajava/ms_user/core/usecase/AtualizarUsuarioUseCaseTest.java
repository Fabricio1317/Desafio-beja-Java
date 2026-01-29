package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.domain.UserRole; // <--- Importante
import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.exception.RegraNegocioException;
import com.becajava.ms_user.core.gateway.PasswordEncoderGateway;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import com.becajava.ms_user.dto.UsuarioRequestDTO;
import com.becajava.ms_user.dto.UsuarioResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarUsuarioUseCaseTest {

    @InjectMocks private AtualizarUsuarioUseCase useCase;
    @Mock private UsuarioGateway usuarioGateway;
    @Mock private PasswordEncoderGateway passwordEncoderGateway;

    // Helper corrigido com UserRole.USER
    private Usuario usuarioAntigo() {
        return new Usuario(1L, "Nome Antigo", "12345678901", "antigo@email.com", "senhaAntiga", UserRole.USER);
    }

    @Test
    @DisplayName("Deve atualizar dados básicos sem trocar senha")
    void sucessoSemTrocarSenha() {
        Usuario antigo = usuarioAntigo();

        // DTO corrigido com UserRole.USER (ou null se preferir testar o default)
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Nome Novo", antigo.getCpf(), antigo.getEmail(), null, UserRole.USER);

        when(usuarioGateway.buscaPorId(1L)).thenReturn(Optional.of(antigo));

        // Mock do retorno corrigido com UserRole.USER
        when(usuarioGateway.atualizar(any())).thenReturn(
                new Usuario(1L, "Nome Novo", antigo.getCpf(), antigo.getEmail(), antigo.getSenha(), UserRole.USER)
        );

        UsuarioResponseDTO resultado = useCase.execute(1L, dto);

        assertEquals("Nome Novo", resultado.nome());

        verify(passwordEncoderGateway, never()).encode(any());
        verify(usuarioGateway).atualizar(any());
    }

    @Test
    @DisplayName("Deve atualizar senha quando informada")
    void sucessoTrocandoSenha() {
        Usuario antigo = usuarioAntigo();
        // DTO corrigido
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Nome", antigo.getCpf(), antigo.getEmail(), "novaSenha123", UserRole.USER);

        when(usuarioGateway.buscaPorId(1L)).thenReturn(Optional.of(antigo));
        when(passwordEncoderGateway.encode("novaSenha123")).thenReturn("hashNovaSenha");

        when(usuarioGateway.atualizar(any())).thenReturn(antigo);

        useCase.execute(1L, dto);

        verify(passwordEncoderGateway).encode("novaSenha123");
        verify(usuarioGateway).atualizar(any());
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar usuário inexistente")
    void erroUsuarioNaoEncontrado() {
        when(usuarioGateway.buscaPorId(99L)).thenReturn(Optional.empty());
        // DTO corrigido
        UsuarioRequestDTO dto = new UsuarioRequestDTO("N", "12345678901", "e@e.com", "S", UserRole.USER);

        assertThrows(RegraNegocioException.class, () -> useCase.execute(99L, dto));

        verify(usuarioGateway, never()).atualizar(any());
    }

    @Test
    @DisplayName("Deve lançar erro se tentar trocar para um email que já existe")
    void erroEmailEmUso() {
        Usuario antigo = usuarioAntigo();

        // DTO corrigido
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Nome", antigo.getCpf(), "outro@email.com", "123", UserRole.USER);

        when(usuarioGateway.buscaPorId(1L)).thenReturn(Optional.of(antigo));
        when(usuarioGateway.existePorEmail("outro@email.com")).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> useCase.execute(1L, dto));

        verify(usuarioGateway, never()).atualizar(any());
    }

    @Test
    @DisplayName("Deve lançar erro se tentar trocar para um CPF que já existe")
    void erroCpfEmUso() {
        Usuario antigo = usuarioAntigo();
        String novoCpf = "98765432100";
        // DTO corrigido
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Nome", novoCpf, antigo.getEmail(), "123", UserRole.USER);

        when(usuarioGateway.buscaPorId(1L)).thenReturn(Optional.of(antigo));
        when(usuarioGateway.existePorCpf(novoCpf)).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> useCase.execute(1L, dto));

        verify(usuarioGateway, never()).atualizar(any());
    }
}