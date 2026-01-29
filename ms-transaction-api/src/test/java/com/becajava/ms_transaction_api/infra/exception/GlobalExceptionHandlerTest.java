package com.becajava.ms_transaction_api.infra.exception;

import com.becajava.ms_transaction_api.core.exception.RegraDeNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveTratarRegraDeNegocioException() {
        RegraDeNegocioException ex = new RegraDeNegocioException("Erro de Teste");
        ResponseEntity<?> response = handler.handleRegraNegocio(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}