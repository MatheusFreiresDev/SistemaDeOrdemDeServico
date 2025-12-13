package com.ordemDeServico.Service;

import com.ordemDeServico.Repository.UsuarioRepository;
import com.ordemDeServico.Service.Event.OrdemServicoStatusAtualizadoEvent;
import com.ordemDeServico.Service.Event.UsuarioCriado;
import com.ordemDeServico.model.Usuario;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final ApplicationEventPublisher eventPublisher;
    private final UsuarioRepository usuarioRepository;

    public UserService(ApplicationEventPublisher eventPublisher, UsuarioRepository usuarioRepository) {
        this.eventPublisher = eventPublisher;
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario criar(Usuario usuario) {
        Usuario usuarioCriado = usuarioRepository.save(usuario);
        eventPublisher.publishEvent(new UsuarioCriado(usuario));
        return usuario;
    }

    public Usuario buscarPorId(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario atualizar(int id, Usuario dados) {
        Usuario usuario = buscarPorId(id);
        usuario.setNome(dados.getNome());
        usuario.setEmail(dados.getEmail());
        usuario.setSenha(dados.getSenha());
        return usuarioRepository.save(usuario);
    }

    public void deletar(int  id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    public Optional<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNome(nome);
    }
}
