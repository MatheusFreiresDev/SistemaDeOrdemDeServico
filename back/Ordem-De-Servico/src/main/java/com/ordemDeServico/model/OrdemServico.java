package com.ordemDeServico.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ordemDeServico.model.enums.CategoriaOS;
import com.ordemDeServico.model.enums.PrioridadeOS;
import com.ordemDeServico.model.enums.StatusOS;
import io.swagger.v3.oas.annotations.media.Schema; // Importação necessária!
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "ordens_servico")
@Schema(description = "Representa uma Ordem de Serviço no sistema, incluindo detalhes, status e responsáveis.") // Documentação da classe
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da Ordem de Serviço.", example = "42")
    private int id;

    @Schema(description = "Data e hora exata da criação da OS.", example = "2025-12-13T15:00:00")
    private LocalDateTime data_criacao;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status atual da OS.", example = "ABERTO", allowableValues = {"ABERTO", "EM_EXECUÇÃO", "CONCLUIDO"})
    private StatusOS status;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Prioridade da OS.", example = "MEDIA", allowableValues = {"BAIXA", "MEDIA", "ALTA"})
    private PrioridadeOS prioridade;

    @Schema(description = "Título resumido da Ordem de Serviço.", example = "Instalação de novo software de gestão.")
    private String titulo;

    @Schema(description = "Descrição detalhada do problema ou serviço requisitado.")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Categoria de classificação da OS.", example = "SUPORTE", allowableValues = {"SUPORTE", "INFRAESTRUTURA", "DESENVOLVIMENTO", "OUTROS"})
    private CategoriaOS categoria;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criador_id")
    @ToString.Exclude
    @JsonIgnore
    @Schema(description = "O Usuário que criou esta Ordem de Serviço (apenas o ID será exposto no JSON).")
    private Usuario criador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "executor_id")
    @ToString.Exclude
    @JsonIgnore
    @Schema(description = "O Usuário responsável pela execução da OS. Nulo se estiver em 'ABERTO'.")
    private Usuario executor;

    @Override
    public String toString() {
        return "OrdemServico{id=" + id + ", titulo='" + titulo + "', status=" + status + "}";
    }

}