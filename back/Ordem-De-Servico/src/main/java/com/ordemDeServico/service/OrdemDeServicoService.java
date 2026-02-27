package com.ordemDeServico.service;

import com.ordemDeServico.exceptions.NotExistException;
import com.ordemDeServico.exceptions.RegraDeNegocioVioladaException;
import com.ordemDeServico.exceptions.UnauthorizedAccessException;
import com.ordemDeServico.service.event.OrdemServicoDeletada;
import com.ordemDeServico.service.event.OrdemServicoStatusAtualizadoEvent;
import com.ordemDeServico.repository.OrdemServicoRepository;
import com.ordemDeServico.repository.UsuarioRepository;
import com.ordemDeServico.model.OrdemServico;
import com.ordemDeServico.model.Usuario;
import com.ordemDeServico.model.enums.StatusOS;
import com.ordemDeServico.model.enums.UserRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemDeServicoService {
    private final UserService userService;
    private final OrdemServicoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    // --- CRIAR ---
    public OrdemServico criar(OrdemServico request) {
        Usuario criador = getUsuarioLogado();
        OrdemServico nova = OrdemServico.builder()
                .data_criacao(LocalDateTime.now())
                .descricao(request.getDescricao())
                .titulo(request.getTitulo())
                .categoria(request.getCategoria())
                .prioridade(request.getPrioridade())
                .status(StatusOS.ABERTO)
                .criador(criador)
                .executor(null)
                .build();
        return repository.save(nova);
    }

    // --- ATUALIZAR ---
    public OrdemServico atualizar(int id, OrdemServico request) {
        Usuario usuario = getUsuarioLogado();

        OrdemServico osDoBanco = repository.findById(id)
                .orElseThrow(() -> new NotExistException("A OS nao existe."));

        if (usuario.getRole() == UserRoles.EXECUTOR) {
            osDoBanco.setExecutor(usuario);
            osDoBanco.setStatus(request.getStatus());
            osDoBanco.setPrioridade(request.getPrioridade());
            osDoBanco.setCategoria(request.getCategoria());
            osDoBanco.setTitulo(request.getTitulo());
            osDoBanco.setDescricao(request.getDescricao());
        } else {
            if (osDoBanco.getCriador().getId() != usuario.getId()) {
                throw new UnauthorizedAccessException("Acesso Negado. Você não criou esta OS.");
            }
            osDoBanco.setTitulo(request.getTitulo());
            osDoBanco.setDescricao(request.getDescricao());
        }
        return repository.save(osDoBanco);
    }

    // --- LISTAR ---
    public List<OrdemServico> listar() {
        Usuario usuarioLogado = getUsuarioLogado();
        String role = usuarioLogado.getAuthority().toString();
        if (role.equals("EXECUTOR")) {
            return repository.findParaExecutor(usuarioLogado.getId());
        } else if (role.equals("CLIENTE")) {
            return repository.findAllByCriadorId((long) usuarioLogado.getId());
        }
        return Collections.EMPTY_LIST;
    }

    // --- BUSCAR POR ID ---
    public OrdemServico buscarPorId(int id) {
        Usuario usuario = getUsuarioLogado();
        OrdemServico os = repository.findById(id)
                .orElseThrow(() -> new NotExistException("OS não encontrada."));
        validarAcessoOS(usuario, os);
        return os;
    }

    // --- DELETAR ---
    public void deletar(int osId) {
        Usuario usuario = getUsuarioLogado();
        OrdemServico os = repository.findById(osId)
                .orElseThrow(() -> new NotExistException("Ordem de Serviço não encontrada."));

        if (usuario.getRole() == UserRoles.EXECUTOR) {
            if (os.getStatus() != StatusOS.CONCLUIDO) {
                throw new RegraDeNegocioVioladaException("A Ordem de serviço deve estar concluída para ser deletada.");
            }
        }
        if (usuario.getRole() == UserRoles.CLIENTE) {
            if (os.getCriador().getId() != usuario.getId()) {
                throw new UnauthorizedAccessException("Acesso Negado.");
            }
            if (os.getStatus() != StatusOS.ABERTO) {
                throw new RegraDeNegocioVioladaException("Acesso Negado.");
            }
        }
        eventPublisher.publishEvent(new OrdemServicoDeletada(os));
        repository.deleteById(osId);
    }

    // --- AVANCAR STATUS ---
    public OrdemServico avancarStatus(Integer osId) {
        Usuario usuario = getUsuarioLogado();
        if (usuario.getRole() != UserRoles.EXECUTOR) {
            throw new UnauthorizedAccessException("Acesso Negado. Somente o EXECUTOR pode avançar o status da OS.");
        }
        OrdemServico os = repository.findById(osId)
                .orElseThrow(() -> new NotExistException("OS não encontrada."));

        if (os.getStatus() != StatusOS.ABERTO) {
            if (os.getExecutor() != null && os.getExecutor().getId() != usuario.getId()) {
                throw new UnauthorizedAccessException("Acesso Negado. O Executor da OS é " + os.getExecutor().getNome() + ".");
            }
        }
        StatusOS proximoStatus;
        try {
            proximoStatus = StatusOS.values()[os.getStatus().ordinal() + 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("A Ordem de Serviço já está no último status possível.");
        }

        if (os.getExecutor() == null) {
            os.setExecutor(usuario);
        }
        eventPublisher.publishEvent(new OrdemServicoStatusAtualizadoEvent(os));
        os.setStatus(proximoStatus);
        return repository.save(os);
    }

    // --- MÉTODOS PRIVADOS ---
    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void validarAcessoOS(Usuario usuario, OrdemServico os) {
        if (usuario.getRole() == UserRoles.CLIENTE) {
            if (os.getCriador().getId() != usuario.getId()) {
                throw new UnauthorizedAccessException("Acesso Negado. Você não criou esta OS.");
            }
        }
    }
}