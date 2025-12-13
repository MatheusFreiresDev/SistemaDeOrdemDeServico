package com.ordemDeServico.Exceptions;

public class JWTCreationException extends RuntimeException {
    public JWTCreationException(String message) {
        super(message);
    }
}
