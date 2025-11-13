package com.ordemDeServico.model.enums;

public enum StatusOS {
    ABERTO("aberto"),
    EM_EXECUCAO("em_execucao"),
    CONCLUIDO("concluido");
    String status;
     StatusOS(String status){
        this.status = status;
    }
}
