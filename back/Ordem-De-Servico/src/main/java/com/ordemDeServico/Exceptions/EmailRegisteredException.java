package com.ordemDeServico.Exceptions;

public class EmailRegisteredException extends RuntimeException {
    public EmailRegisteredException(String message) {
        super(message);
    }
}
