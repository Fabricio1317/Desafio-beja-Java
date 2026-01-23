package com.becajava.ms_user.infra.controller;

import com.becajava.ms_user.core.usecase.LoginUsuarioUseCase;
import com.becajava.ms_user.dto.LoginRequestDTO;
import com.becajava.ms_user.dto.LoginResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUsuarioUseCase loginUsuarioUseCase;

    public AuthController(LoginUsuarioUseCase loginUsuarioUseCase) {
        this.loginUsuarioUseCase = loginUsuarioUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto){
        LoginResponseDTO token = loginUsuarioUseCase.execute(dto);
        return ResponseEntity.ok(token);
    }
}
