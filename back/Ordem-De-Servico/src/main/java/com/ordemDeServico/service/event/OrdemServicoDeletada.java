package com.ordemDeServico.service.event;

import com.ordemDeServico.model.OrdemServico;

public class OrdemServicoDeletada {
    private final OrdemServico ordemServico;
    public OrdemServicoDeletada(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }
    public OrdemServico getOrdemServico() {
        return ordemServico;
    }
}
