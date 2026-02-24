package com.ordemDeServico.configSecurity; // Use o pacote correto do seu projeto

import com.ordemDeServico.dtos.ExceptionResponse;
import com.ordemDeServico.exceptions.EmailRegisteredException;
import com.ordemDeServico.exceptions.JWTCreationException;
import com.ordemDeServico.exceptions.NotExistException;
import com.ordemDeServico.exceptions.RegraDeNegocioVioladaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
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