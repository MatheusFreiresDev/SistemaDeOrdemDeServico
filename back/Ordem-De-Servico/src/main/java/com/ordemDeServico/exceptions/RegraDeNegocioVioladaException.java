package com.ordemDeServico.exceptions;

public class RegraDeNegocioVioladaException extends RuntimeException {
  public RegraDeNegocioVioladaException(String message) {
    super(message);
  }
}
