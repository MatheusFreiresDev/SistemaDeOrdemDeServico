package com.ordemDeServico.exceptions;

public class JWTCreationException extends RuntimeException {
    public JWTCreationException(String message) {
        super(message);
    }
}
