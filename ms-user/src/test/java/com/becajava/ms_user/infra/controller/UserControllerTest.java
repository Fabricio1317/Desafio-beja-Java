package com.becajava.ms_user.infra.controller;

import com.becajava.ms_user.core.domain.UserRole; // <--- Importante
import com.becajava.ms_user.core.usecase.*;
import com.becajava.ms_user.dto.UsuarioRequestDTO;
import com.becajava.ms_user.dto.UsuarioResponseDTO;
import com.becajava.ms_user.infra.service.ExcelImportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private CriarUsuarioUseCase criarUsuarioUseCase;
    @MockBean private DeletarUseCase deletarUseCase;
    @MockBean private BuscarUsuarioUseCase buscarUsuarioUseCase;
    @MockBean private AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    @MockBean private ExcelImportService excelImportService;

    // --- HELPERS CORRIGIDOS ---

    private UsuarioRequestDTO criarRequestDTO() {
        // Adicionado UserRole.USER no final
        return new UsuarioRequestDTO("Teste", "12345678901", "teste@email.com", "123456", UserRole.USER);
    }

    private UsuarioResponseDTO criarResponseDTO() {
        // Adicionado UserRole.USER no final
        return new UsuarioResponseDTO(1L, "Teste", "12345678901", "teste@email.com", UserRole.USER);
    }

    // --- TESTES ---

    @Test
    @DisplayName("POST /users - Deve criar usuário e retornar 201")
    void criarUsuario() throws Exception {
        UsuarioRequestDTO request = criarRequestDTO();
        UsuarioResponseDTO response = criarResponseDTO();

        when(criarUsuarioUseCase.execute(any(UsuarioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Teste"))
                .andExpect(jsonPath("$.email").value("teste@email.com"))
                .andExpect(jsonPath("$.role").value("USER")); // Verifica se retornou a role
    }

    @Test
    @DisplayName("GET /users/{id} - Deve retornar usuário e status 200")
    void buscarUsuario() throws Exception {
        UsuarioResponseDTO response = criarResponseDTO();

        when(buscarUsuarioUseCase.execute(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    @DisplayName("PUT /users/{id} - Deve atualizar e retornar 200")
    void atualizarUsuario() throws Exception {
        UsuarioRequestDTO request = criarRequestDTO();
        UsuarioResponseDTO response = criarResponseDTO();

        when(atualizarUsuarioUseCase.execute(eq(1L), any(UsuarioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Teste"));
    }

    @Test
    @DisplayName("DELETE /users/{id} - Deve deletar e retornar 204")
    void deletarUsuario() throws Exception {
        doNothing().when(deletarUseCase).execute(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    @DisplayName("POST /users/importar - Deve fazer upload de Excel e retornar lista de mensagens")
    void importarExcel() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "usuarios.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "conteudo-fake".getBytes()
        );

        List<String> resultadoEsperado = List.of("Usuário 1 criado", "Usuário 2 erro");

        when(excelImportService.importarUsuarios(any())).thenReturn(resultadoEsperado);

        mockMvc.perform(multipart("/users/importar")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Usuário 1 criado"));
    }
}