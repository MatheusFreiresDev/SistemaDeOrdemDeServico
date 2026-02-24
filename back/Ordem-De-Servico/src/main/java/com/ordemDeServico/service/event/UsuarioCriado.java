package com.ordemDeServico.service.event;

import com.ordemDeServico.model.Usuario;

public class UsuarioCriado {
    private final Usuario usuario;

    public UsuarioCriado(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
