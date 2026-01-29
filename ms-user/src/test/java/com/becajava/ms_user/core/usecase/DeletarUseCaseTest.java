package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.domain.UserRole; // <--- Importante
import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.exception.RegraNegocioException;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletarUseCaseTest {

    @InjectMocks private DeletarUseCase useCase;
    @Mock private UsuarioGateway usuarioGateway;

    private Usuario criarUsuarioValido() {
        // Construtor corrigido com UserRole.USER
        return new Usuario(1L, "Nome Teste", "12345678901", "teste@email.com", "senha123", UserRole.USER);
    }

    @Test
    @DisplayName("Deve deletar usuário existente")
    void deletarSucesso() {
        Usuario usuario = criarUsuarioValido();
        when(usuarioGateway.buscaPorId(1L)).thenReturn(Optional.of(usuario));

        useCase.execute(1L);

        verify(usuarioGateway).deletar(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar deletar ID inexistente")
    void deletarErro() {
        when(usuarioGateway.buscaPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RegraNegocioException.class, () -> useCase.execute(1L));

        verify(usuarioGateway, never()).deletar(any());
    }
}