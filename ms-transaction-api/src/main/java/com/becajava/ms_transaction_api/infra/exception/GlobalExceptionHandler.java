package com.becajava.ms_transaction_api.infra.exception;

import com.becajava.ms_transaction_api.core.exception.RegraDeNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Tratamento para Erros de Negócio (400)
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<MensagemErro> handleRegraNegocio(RegraDeNegocioException e, HttpServletRequest request) {
        MensagemErro erro = new MensagemErro(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Regra de Negócio",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // Tratamento para Validação de Campos (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MensagemErro> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String mensagens = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        MensagemErro erro = new MensagemErro(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                mensagens,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // Tratamento Genérico (500) - Pega qualquer outro erro não tratado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensagemErro> handleGeneral(Exception e, HttpServletRequest request) {
        MensagemErro erro = new MensagemErro(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}