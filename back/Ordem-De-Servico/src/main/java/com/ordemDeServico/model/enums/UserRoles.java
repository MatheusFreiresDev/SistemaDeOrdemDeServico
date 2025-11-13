package com.ordemDeServico.model.enums;

public enum UserRoles {
    CLIENTE("cliente"),
    EXECUTOR("executor");
    String role;
    UserRoles(String role){
        this.role = role;
    }
}

