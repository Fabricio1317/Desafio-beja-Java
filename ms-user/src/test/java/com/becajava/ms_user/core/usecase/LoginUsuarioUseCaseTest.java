package com.becajava.ms_user.core.usecase;

import com.becajava.ms_user.core.domain.UserRole; // <--- Importante
import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.exception.RegraNegocioException;
import com.becajava.ms_user.core.gateway.PasswordEncoderGateway;
import com.becajava.ms_user.core.gateway.TokenGateway;
import com.becajava.ms_user.core.gateway.UsuarioGateway;
import com.becajava.ms_user.dto.LoginRequestDTO;
import com.becajava.ms_user.dto.LoginResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUsuarioUseCaseTest {

    @InjectMocks private LoginUsuarioUseCase useCase;
    @Mock private UsuarioGateway usuarioGateway;
    @Mock private PasswordEncoderGateway passwordEncoderGateway;
    @Mock private TokenGateway tokenGateway;

    @Test
    @DisplayName("Deve logar com sucesso e retornar token")
    void loginSucesso() {
        // Construtor corrigido com UserRole.USER
        Usuario usuario = new Usuario(1L, "Nome", "12345678901", "teste@email.com", "senhaHash", UserRole.USER);
        LoginRequestDTO dto = new LoginRequestDTO("teste@email.com", "123");

        when(usuarioGateway.buscaPorEmail(dto.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoderGateway.matches(dto.senha(), usuario.getSenha())).thenReturn(true);
        when(tokenGateway.gerarToken(usuario)).thenReturn("token.jwt.valido");

        LoginResponseDTO res = useCase.execute(dto);

        assertNotNull(res);
        assertEquals("token.jwt.valido", res.token());
    }

    @Test
    @DisplayName("Erro: Email não encontrado")
    void emailInvalido() {
        when(usuarioGateway.buscaPorEmail(any())).thenReturn(Optional.empty());
        LoginRequestDTO dto = new LoginRequestDTO("errado@teste.com", "123");

        assertThrows(RegraNegocioException.class, () -> useCase.execute(dto));
    }
}