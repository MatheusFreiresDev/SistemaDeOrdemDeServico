package com.ordemDeServico.model;

import com.ordemDeServico.model.enums.CategoriaOS;
import com.ordemDeServico.model.enums.PrioridadeOS;
import com.ordemDeServico.model.enums.StatusOS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "ordens_servico")
public class OrdemServico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime data_criacao;
    private StatusOS status;
    private PrioridadeOS prioridade;
    private String titulo;
    private String descricao;
    private CategoriaOS categoria;
    @ManyToOne
    @JoinColumn(name = "criador_id")
    private Usuario criador;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private Usuario executor;



}
