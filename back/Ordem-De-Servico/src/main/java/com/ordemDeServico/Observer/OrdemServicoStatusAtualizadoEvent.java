package com.ordemDeServico.Observer;

import com.ordemDeServico.model.OrdemServico;

// O Evento que será publicado quando o status mudar.
public class OrdemServicoStatusAtualizadoEvent {

    private final OrdemServico ordemServico;

    public OrdemServicoStatusAtualizadoEvent(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }
}