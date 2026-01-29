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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarUsuarioUseCaseTest {

    @InjectMocks private CriarUsuarioUseCase useCase;
    @Mock private UsuarioGateway usuarioGateway;
    @Mock private PasswordEncoderGateway passwordEncoderGateway;

    private UsuarioRequestDTO dtoMock() {
        // DTO corrigido com UserRole.USER
        return new UsuarioRequestDTO("Teste", "76045624020", "teste@email.com", "123456", UserRole.USER);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso quando dados são válidos")
    void deveCriarUsuario() {
        UsuarioRequestDTO dto = dtoMock();

        when(usuarioGateway.existePorCpf(dto.cpf())).thenReturn(false);
        when(usuarioGateway.existePorEmail(dto.email())).thenReturn(false);
        when(passwordEncoderGateway.encode(dto.senha())).thenReturn("hashSenha");

        // Objeto corrigido com UserRole.USER
        Usuario usuarioSalvo = new Usuario(1L, dto.nome(), dto.cpf(), dto.email(), "hashSenha", UserRole.USER);

        when(usuarioGateway.criarUsuario(any())).thenReturn(usuarioSalvo);

        UsuarioResponseDTO resultado = useCase.execute(dto);

        assertNotNull(resultado);
        assertEquals(dto.email(), resultado.email());
        verify(usuarioGateway).criarUsuario(any());
        verify(passwordEncoderGateway).encode(dto.senha());
    }

    @Test
    @DisplayName("Deve lançar erro quando CPF já existe")
    void erroCpfDuplicado() {
        UsuarioRequestDTO dto = dtoMock();
        when(usuarioGateway.existePorCpf(dto.cpf())).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> useCase.execute(dto));
        verify(usuarioGateway, never()).criarUsuario(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando Email já existe")
    void erroEmailDuplicado() {
        UsuarioRequestDTO dto = dtoMock();
        when(usuarioGateway.existePorCpf(dto.cpf())).thenReturn(false);
        when(usuarioGateway.existePorEmail(dto.email())).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> useCase.execute(dto));
        verify(usuarioGateway, never()).criarUsuario(any());
    }
}