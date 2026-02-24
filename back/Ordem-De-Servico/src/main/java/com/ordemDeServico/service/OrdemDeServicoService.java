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

    public OrdemServico criar(OrdemServico request) {
        Object criador = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        request.setCriador((Usuario) criador);
        Usuario executor = null;
        OrdemServico nova = OrdemServico.builder()
                .data_criacao(LocalDateTime.now())
                .descricao(request.getDescricao())
                .titulo(request.getTitulo())
                .categoria(request.getCategoria())
                .prioridade(request.getPrioridade())
                .status(StatusOS.ABERTO)
                .criador((Usuario) criador)
                .executor(executor)
                .build();
        return repository.save(nova);
    }

    // --- ATUALIZAR ---
    public OrdemServico atualizar(int id,OrdemServico request) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        OrdemServico osDoBanco = repository.findById(id)
                .orElseThrow(() -> new NotExistException("A OS nao existe."));

        Integer idOriginal = osDoBanco.getCriador().getId();
        Integer idLogado = usuario.getId();

        if (usuario.getRole() == UserRoles.EXECUTOR) {
            osDoBanco.setExecutor(usuario);
            osDoBanco.setStatus(request.getStatus());
            osDoBanco.setPrioridade(request.getPrioridade());
            osDoBanco.setCategoria(request.getCategoria());
            osDoBanco.setTitulo(request.getTitulo());
            osDoBanco.setDescricao(request.getDescricao());
        } else {
            if (!idOriginal.equals(idLogado)) {
                throw new UnauthorizedAccessException("Acesso Negado. Você não criou esta OS.");
            }
            osDoBanco.setTitulo(request.getTitulo());
            osDoBanco.setDescricao(request.getDescricao());
        }
   return  repository.save(osDoBanco);
    }
    // --- LISTAR ---
    public List<OrdemServico> listar() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if( user instanceof Usuario usuarioLogado) {
            String role = usuarioLogado.getAuthority().toString();
            if(role.equals("EXECUTOR")){
                return repository.findParaExecutor(((Usuario) user).getId());
            } else if (role.equals("CLIENTE")){
                return repository.findAllByCriadorId((long) usuarioLogado.getId());
            }
            return Collections.EMPTY_LIST;
        }

        return repository.findAll();
    }

    // --- BUSCAR POR ID ---
    public OrdemServico buscarPorId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));
    }

    // --- DELETAR ---
    public void deletar(int osId) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OrdemServico os = repository.findById(osId)
                .orElseThrow(() -> new NotExistException("Ordem de Serviço não encontrada."));

        Integer idCriadorOS = os.getCriador().getId();
        Integer userId = usuario.getId();

        if(usuario.getRole() == UserRoles.EXECUTOR){
            if (os.getStatus() != StatusOS.CONCLUIDO) {
                throw new RegraDeNegocioVioladaException("A Ordem de serviço deve está concluida para ser deletada.");
            }
        }
        if(usuario.getRole() == UserRoles.CLIENTE){
            if (!idCriadorOS.equals(userId)){
                throw new UnauthorizedAccessException("Acesso Negado.");
            }
            if(os.getStatus() != StatusOS.ABERTO){
                throw new RegraDeNegocioVioladaException("Acesso Negado.");
            }

        }
        eventPublisher.publishEvent(new OrdemServicoDeletada(os));
        repository.deleteById(osId);
    }


        public OrdemServico avancarStatus(Integer osId) {
            Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (usuario.getRole() != UserRoles.EXECUTOR) {
                throw new UnauthorizedAccessException("Acesso Negado. Somente o EXECUTOR pode avançar o status da OS.");
            }
            OrdemServico os = buscarPorId(osId);
            StatusOS statusAtual = os.getStatus();
            Integer idDoUser = usuario.getId();
            if (os.getStatus() != StatusOS.ABERTO){
                if(os.getExecutor() != null && !idDoUser.equals((Integer) os.getExecutor().getId())){
                    throw new UnauthorizedAccessException("Acesso Negado. O Executor da os é " + os.getExecutor().getNome() + ".");
                }
            }
            StatusOS proximoStatus;
            try {
                int proximoOrdinal = statusAtual.ordinal() + 1;
                proximoStatus = StatusOS.values()[proximoOrdinal];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException("A Ordem de Serviço já está no último status possível.");
            }

            if(os.getExecutor() == null) {
                os.setExecutor(usuario);
            }
            eventPublisher.publishEvent(new OrdemServicoStatusAtualizadoEvent(os));
            os.setStatus(proximoStatus);
            return repository.save(os);
        }


}
