package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.domain.UserRole; // <--- Importante
import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.exception.RegraNegocioException;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import com.becajava.ms_user.dto.UsuarioResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarUsuarioUseCaseTest {

    @InjectMocks private BuscarUsuarioUseCase useCase;
    @Mock private UsuarioGateway usuarioGateway;

    @Test
    @DisplayName("Deve retornar DTO quando usuário existe")
    void buscaSucesso() {
        // Construtor corrigido com UserRole.USER
        Usuario usuario = new Usuario(1L, "Teste", "12345678901", "teste@email.com", "senhaHash", UserRole.USER);

        when(usuarioGateway.buscaPorId(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO res = useCase.execute(1L);

        assertNotNull(res);
        assertEquals(1L, res.id());
        assertEquals("teste@email.com", res.email());
    }

    @Test
    @DisplayName("Deve lançar Exception quando usuário não existe")
    void buscaErro() {
        when(usuarioGateway.buscaPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RegraNegocioException.class, () -> useCase.execute(1L));
    }
}