package com.becajava.ms_user.infra.controller;

import com.becajava.ms_user.core.usecase.AtualizarUsuarioUseCase;
import com.becajava.ms_user.core.usecase.BuscarUsuarioUseCase;
import com.becajava.ms_user.core.usecase.CriarUsuarioUseCase;
import com.becajava.ms_user.core.usecase.DeletarUseCase;
import com.becajava.ms_user.dto.UsuarioRequestDTO;
import com.becajava.ms_user.dto.UsuarioResponseDTO;
import com.becajava.ms_user.infra.service.ExcelImportService; // <--- Importante!
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final DeletarUseCase deletarUseCase;
    private final BuscarUsuarioUseCase buscarUsuarioUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;

    private final ExcelImportService excelImportService;

    public UserController(CriarUsuarioUseCase criarUsuarioUseCase,
                          DeletarUseCase deletarUseCase,
                          BuscarUsuarioUseCase buscarUsuarioUseCase,
                          AtualizarUsuarioUseCase atualizarUsuarioUseCase,
                          ExcelImportService excelImportService) {
        this.criarUsuarioUseCase = criarUsuarioUseCase;
        this.deletarUseCase = deletarUseCase;
        this.buscarUsuarioUseCase = buscarUsuarioUseCase;
        this.atualizarUsuarioUseCase = atualizarUsuarioUseCase;
        this.excelImportService = excelImportService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO dto){
        UsuarioResponseDTO novoUsuario = criarUsuarioUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }


    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> importarExcel(@RequestParam("file") MultipartFile file) {
        List<String> resultado = excelImportService.importarUsuarios(file);
        return ResponseEntity.ok(resultado);
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
        UsuarioResponseDTO response = atualizarUsuarioUseCase.execute(id, dto);
        return ResponseEntity.ok(response);
    }
}