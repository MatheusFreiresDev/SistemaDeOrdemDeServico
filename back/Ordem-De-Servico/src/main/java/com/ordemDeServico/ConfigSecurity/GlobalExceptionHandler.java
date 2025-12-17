package com.ordemDeServico.ConfigSecurity; // Use o pacote correto do seu projeto

import com.ordemDeServico.DTOS.ExceptionResponse;
import com.ordemDeServico.DTOS.RegisterRequest;
import com.ordemDeServico.Exceptions.EmailRegisteredException;
import com.ordemDeServico.Exceptions.JWTCreationException;
import com.ordemDeServico.Exceptions.NotExistException;
import com.ordemDeServico.Exceptions.RegraDeNegocioVioladaException;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException; // Importante: java.nio ou org.springframework.security
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        HttpStatus statusHttp = HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                LocalDateTime.now(),
                statusHttp.value(),
                statusHttp.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false)
        );


        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDenied(Exception ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                webRequest.getDescription(false)
        );

        return new ResponseEntity<>(exceptionResponse,HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(RegraDeNegocioVioladaException.class)
    public ResponseEntity<ExceptionResponse> regraDeNegocioViolada(Exception ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                webRequest.getDescription(false)
        );

        return new ResponseEntity<>(exceptionResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotExistException.class)
    public ResponseEntity<ExceptionResponse> notExist(Exception ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                webRequest.getDescription(false)
        );

        return new ResponseEntity<>(exceptionResponse,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(JWTCreationException.class)
    public ResponseEntity<ExceptionResponse> handleJwtCreationException(JWTCreationException ex, WebRequest request) {

        HttpStatus statusHttp = HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                LocalDateTime.now(),
                statusHttp.value(),
                statusHttp.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(exceptionResponse, statusHttp); // Retorna 500
    }
    @ExceptionHandler(EmailRegisteredException.class)
    public ResponseEntity<ExceptionResponse> handleEmailRegisteredException(EmailRegisteredException ex, WebRequest webRequest) {
        HttpStatus statusHttp = HttpStatus.BAD_REQUEST;
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                LocalDateTime.now(),
                statusHttp.value(),
                statusHttp.getReasonPhrase(),
                ex.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(exceptionResponse, statusHttp.BAD_REQUEST);
    }
}