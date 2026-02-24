package com.ordemDeServico.dtos;

import java.time.LocalDateTime;

public record ExceptionResponse(
    LocalDateTime timestamp,
    int status,
    String erro,
    String message,
    String patch

) {

}
