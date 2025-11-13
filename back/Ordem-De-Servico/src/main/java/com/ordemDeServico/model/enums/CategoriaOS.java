package com.ordemDeServico.model.enums;

public enum CategoriaOS {
    RECLAMACAO("Reclamação"),
    MANUTENCAO("Manutenção"),
    DUVIDA("Duvida");
    String categoria;
    CategoriaOS (String categoria) {
        this.categoria = categoria;
    }
}
