package com.ordemDeServico.model.enums;

public enum PrioridadeOS {
    ALTA("alta"),
    MEDIA("media"),
    BAIXA("baixa");

    String prioridade;

    PrioridadeOS (String prioridade) {
        this.prioridade = prioridade;
    }

}
