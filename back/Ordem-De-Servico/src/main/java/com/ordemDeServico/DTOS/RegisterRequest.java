package com.ordemDeServico.DTOS;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Estrutura de dados necessária para o registro de um novo usuário (Perfil Cliente).")
public record RegisterRequest(

        @Schema(description = "Endereço de e-mail para registro.", example = "matheus.novo@teste.com")
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O formato do e-mail é inválido.")
        String email,

        @Schema(description = "Senha escolhida (deve ser forte).", example = "SenhaSegura123")
        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
        String senha,

        @Schema(description = "Nome completo do novo usuário.", example = "Matheus Freires")
        @NotBlank(message = "O nome é obrigatório.")
        String nome

) {
}