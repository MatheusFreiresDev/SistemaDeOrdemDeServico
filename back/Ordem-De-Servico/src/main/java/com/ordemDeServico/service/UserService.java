package com.ordemDeServico.service;

import com.ordemDeServico.exceptions.EmailRegisteredException;
import com.ordemDeServico.exceptions.NotExistException;
import com.ordemDeServico.exceptions.UnauthorizedAccessException;
import com.ordemDeServico.repository.UsuarioRepository;
import com.ordemDeServico.service.event.UsuarioCriado;
import com.ordemDeServico.model.Usuario;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.rmi.AccessException;
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
        if(usuarioRepository.findByEmail(usuario.getEmail()).isPresent()){
            throw new EmailRegisteredException("Email ja cadastrado.");
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(usuario.getPassword());
        usuario.setSenha(encryptedPassword);
        Usuario usuarioCriado = usuarioRepository.save(usuario);
        eventPublisher.publishEvent(new UsuarioCriado(usuario));
        return usuario;
    }

    public Usuario buscarPorId(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotExistException("Usuário não encontrado"));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario atualizar(int id, Usuario dados) {
        Usuario usuario = buscarPorId(id);
        Usuario logado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!logado.getUsername().equals(usuario.getUsername())) {
            throw new UnauthorizedAccessException("Acesso Negado.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(dados.getPassword());
        usuario.setNome(dados.getNome());
        usuario.setEmail(dados.getEmail());
        usuario.setSenha(dados.getSenha());
        usuario.setSenha(encryptedPassword);
        return usuarioRepository.save(usuario);
    }

    public void deletar(int  id) {
        Usuario usuario = buscarPorId(id);
        Usuario logado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!logado.getUsername().equals(usuario.getUsername())) {
            throw new UnauthorizedAccessException("Acesso Negado.");
        }
        usuarioRepository.delete(usuario);
    }

    public Optional<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNome(nome);
    }
}
