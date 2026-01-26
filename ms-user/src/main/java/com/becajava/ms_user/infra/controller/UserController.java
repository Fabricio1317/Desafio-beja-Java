package com.becajava.ms_user.infra.controller;

import com.becajava.ms_user.core.usecase.AtualizarUsuarioUseCase;
import com.becajava.ms_user.core.usecase.BuscarUsuarioUseCase;
import com.becajava.ms_user.core.usecase.CriarUsuarioUseCase;
import com.becajava.ms_user.core.usecase.DeletarUseCase;
import com.becajava.ms_user.dto.UsuarioRequestDTO;
import com.becajava.ms_user.dto.UsuarioResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final DeletarUseCase deletarUseCase;
    private final BuscarUsuarioUseCase buscarUsuarioUseCase;
    // 1. Faltava declarar essa variável aqui em cima:
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;

    public UserController(CriarUsuarioUseCase criarUsuarioUseCase,
                          DeletarUseCase deletarUseCase,
                          BuscarUsuarioUseCase buscarUsuarioUseCase,
                          AtualizarUsuarioUseCase atualizarUsuarioUseCase) {
        this.criarUsuarioUseCase = criarUsuarioUseCase;
        this.deletarUseCase = deletarUseCase;
        this.buscarUsuarioUseCase = buscarUsuarioUseCase;
        // 2. Completei a atribuição que estava cortada:
        this.atualizarUsuarioUseCase = atualizarUsuarioUseCase;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO dto){
        UsuarioResponseDTO novoUsuario = criarUsuarioUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        deletarUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscar(@PathVariable Long id){
        UsuarioResponseDTO usuario = buscarUsuarioUseCase.execute(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioRequestDTO dto) {
        // 3. Corrigido de "atualizar.execute" para o nome do UseCase correto:
        UsuarioResponseDTO response = atualizarUsuarioUseCase.execute(id, dto);
        return ResponseEntity.ok(response);
    }
}