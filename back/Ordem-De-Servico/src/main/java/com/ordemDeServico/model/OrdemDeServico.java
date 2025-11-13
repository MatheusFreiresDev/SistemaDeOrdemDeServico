package com.ordemDeServico.model;

import com.ordemDeServico.model.enums.CategoriaOS;
import com.ordemDeServico.model.enums.PrioridadeOS;
import com.ordemDeServico.model.enums.StatusOS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ordens_servico")
public class OrdemDeServico {
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
    private User criador;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;



}
