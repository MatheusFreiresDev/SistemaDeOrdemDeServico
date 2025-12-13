package com.ordemDeServico.Service.Event;

import com.ordemDeServico.model.OrdemServico;
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
