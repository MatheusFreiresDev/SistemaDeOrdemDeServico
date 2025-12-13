package com.ordemDeServico.Exceptions;

import java.util.function.Supplier;

public class NotExistException extends RuntimeException {
    public NotExistException(String message) {
        super(message);
    }
}
