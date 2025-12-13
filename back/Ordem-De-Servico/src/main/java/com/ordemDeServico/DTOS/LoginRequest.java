package com.ordemDeServico.DTOS;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Estrutura de dados necessária para autenticação de usuário (Login).")
public record LoginRequest(

        @Schema(description = "Endereço de e-mail do usuário.", example = "usuario@empresa.com.br")
        @NotBlank(message = "O e-mail é obrigatório para o login.")
        @Email(message = "O formato do e-mail é inválido.")
        String email,

        @Schema(description = "Senha do usuário.", example = "123456")
        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {
}