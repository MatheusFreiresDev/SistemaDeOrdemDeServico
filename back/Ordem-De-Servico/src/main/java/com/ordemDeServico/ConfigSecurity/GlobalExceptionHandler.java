package com.ordemDeServico.ConfigSecurity; // Use o pacote correto do seu projeto

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException; // Importante: java.nio ou org.springframework.security
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 1. Pega erros gerais de Regra de Negócio (RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        // Monta o JSON { "message": "O Executor da os é teste" }
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("error", "Erro de Regra de Negócio");

        // Retorna 400 (Bad Request) ou 403 dependendo da sua lógica
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 2. Pega erros específicos de Segurança (Caso sua exception estenda AccessDeniedException)
    // Se o import AccessDeniedException ficar vermelho, verifique se é do Spring Security
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage()); // Aqui vai sua mensagem!
        body.put("error", "Acesso Negado");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // 3. Pega qualquer outro erro não tratado (Exception genérica)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Erro interno no servidor: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}