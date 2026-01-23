package com.becajava.ms_user.infra.controller;

import com.becajava.ms_user.core.usecase.AtualizarSaldoUseCase;
import com.becajava.ms_user.core.usecase.BuscarUsuarioUseCase;
import com.becajava.ms_user.core.usecase.CriarUsuarioUseCase;
import com.becajava.ms_user.core.usecase.DeletarUseCase;
import com.becajava.ms_user.dto.UsuarioRequestDTO;
import com.becajava.ms_user.dto.UsuarioResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final AtualizarSaldoUseCase atualizarUseCase;
    private final DeletarUseCase deletarUseCase;
    private final BuscarUsuarioUseCase buscarUsuarioUseCase;


    public UserController(CriarUsuarioUseCase criarUsuarioUseCase, AtualizarSaldoUseCase atualizarSaldoUseCase, DeletarUseCase deletarUseCase, BuscarUsuarioUseCase buscarUsuarioUseCase) {
        this.criarUsuarioUseCase = criarUsuarioUseCase;
        this.atualizarUseCase = atualizarSaldoUseCase;
        this.deletarUseCase = deletarUseCase;
        this.buscarUsuarioUseCase = buscarUsuarioUseCase;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO dto){
        UsuarioResponseDTO novoUsuario = criarUsuarioUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @PutMapping("/{id}/atualizar-saldo")
    public ResponseEntity<Void> atualizarSaldo(@PathVariable Long id, @RequestParam BigDecimal novoSaldo) {
        atualizarUseCase.execute(id, novoSaldo);
        return ResponseEntity.ok().build();
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


}
