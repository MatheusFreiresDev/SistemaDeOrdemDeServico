package com.ordemDeServico.Exceptions;

public class RegraDeNegocioVioladaException extends RuntimeException {
  public RegraDeNegocioVioladaException(String message) {
    super(message);
  }
}
