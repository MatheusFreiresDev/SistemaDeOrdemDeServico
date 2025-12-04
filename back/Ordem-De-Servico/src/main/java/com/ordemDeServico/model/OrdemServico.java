package com.ordemDeServico.model;

import com.ordemDeServico.model.enums.CategoriaOS;
import com.ordemDeServico.model.enums.PrioridadeOS;
import com.ordemDeServico.model.enums.StatusOS;
import jakarta.persistence.*;
import lombok.*;

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

    @Enumerated(EnumType.STRING)
    private StatusOS status;

    @Enumerated(EnumType.STRING)
    private PrioridadeOS prioridade;

    private String titulo;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private CategoriaOS categoria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criador_id")
    @ToString.Exclude       // evita loop
    private Usuario criador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "executor_id")
    @ToString.Exclude       // evita loop
    private Usuario executor;

    @Override
    public String toString() {
        return "OrdemServico{id=" + id + ", titulo='" + titulo + "', status=" + status + "}";
    }

}
