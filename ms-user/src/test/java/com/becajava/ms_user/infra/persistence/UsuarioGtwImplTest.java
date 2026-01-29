package com.becajava.ms_user.infra.persistence;

import com.becajava.ms_user.core.domain.UserRole; // <--- Importante
import com.becajava.ms_user.core.domain.Usuario;
import com.becajava.ms_user.core.exception.RegraNegocioException;
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
class UsuarioGtwImplTest {

    @InjectMocks
    private UsuarioGtwImpl usuarioGtw;

    @Mock
    private UsuarioRepository repository;

    // --- MÉTODOS AUXILIARES CORRIGIDOS ---

    private UsuarioEntity gerarEntityValida() {
        // Adicionado UserRole.USER
        return new UsuarioEntity(1L, "Teste", "12345678901", "teste@email.com", "senhaHash", UserRole.USER);
    }

    private Usuario gerarUsuarioValido() {
        // Adicionado UserRole.USER
        return new Usuario(1L, "Teste", "12345678901", "teste@email.com", "senhaHash", UserRole.USER);
    }

    // --- TESTES ---

    @Test
    @DisplayName("Deve salvar usuário e retornar objeto de domínio")
    void criarUsuario() {
        Usuario usuarioInput = gerarUsuarioValido();
        UsuarioEntity entitySalva = gerarEntityValida();

        when(repository.save(any(UsuarioEntity.class))).thenReturn(entitySalva);

        Usuario resultado = usuarioGtw.criarUsuario(usuarioInput);

        assertNotNull(resultado);
        assertEquals("12345678901", resultado.getCpf());
        assertEquals(UserRole.USER, resultado.getRole()); // Validação extra
        verify(repository).save(any(UsuarioEntity.class));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao falhar no salvamento")
    void criarUsuarioErro() {
        Usuario usuarioInput = gerarUsuarioValido();

        when(repository.save(any())).thenThrow(new RuntimeException("Erro DB"));

        assertThrows(RegraNegocioException.class, () -> usuarioGtw.criarUsuario(usuarioInput));
    }

    @Test
    @DisplayName("Deve retornar true se CPF existe")
    void existePorCpf() {
        when(repository.existsByCpf("12345678901")).thenReturn(true);
        assertTrue(usuarioGtw.existePorCpf("12345678901"));
    }

    @Test
    @DisplayName("Deve buscar por ID e mapear corretamente")
    void buscaPorId() {
        UsuarioEntity entity = gerarEntityValida();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Usuario> resultado = usuarioGtw.buscaPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Teste", resultado.get().getNome());
        assertEquals(UserRole.USER, resultado.get().getRole());
    }

    @Test
    @DisplayName("Deve retornar vazio se ID não existe")
    void buscaPorIdVazio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioGtw.buscaPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void atualizarUsuario() {
        Usuario usuarioInput = gerarUsuarioValido();
        UsuarioEntity entitySalva = gerarEntityValida();

        when(repository.save(any(UsuarioEntity.class))).thenReturn(entitySalva);

        Usuario resultado = usuarioGtw.atualizar(usuarioInput);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deletarUsuario() {
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> usuarioGtw.deletar(1L));

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar deletar")
    void deletarUsuarioErro() {
        doThrow(new RuntimeException("Erro BD")).when(repository).deleteById(1L);

        assertThrows(RegraNegocioException.class, () -> usuarioGtw.deletar(1L));
    }
}