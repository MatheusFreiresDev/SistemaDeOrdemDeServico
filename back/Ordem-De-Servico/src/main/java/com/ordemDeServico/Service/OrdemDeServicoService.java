package com.ordemDeServico.Service;

import com.ordemDeServico.Service.Event.OrdemServicoStatusAtualizadoEvent;
import com.ordemDeServico.Repository.OrdemServicoRepository;
import com.ordemDeServico.Repository.UsuarioRepository;
import com.ordemDeServico.model.OrdemServico;
import com.ordemDeServico.model.Usuario;
import com.ordemDeServico.model.enums.StatusOS;
import com.ordemDeServico.model.enums.UserRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemDeServicoService {


    private final UserService userService;
    private final OrdemServicoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    // --- CRIAR ---
    public OrdemServico criar(String criadorIdString, OrdemServico request) {

        // 1. Converte o ID da URL (String) para Long (se o ID do seu usuário for Long)
        Long criadorId = Long.valueOf(criadorIdString);

        // 2. Busca o objeto Usuario (Criador) usando o ID da URL
        Usuario criador = usuarioRepository.findById(Integer.valueOf(criadorIdString))
                .orElseThrow(() -> new RuntimeException("Criador não encontrado"));

        // 3. 🚨 RESOLUÇÃO DO NPE: Anexa o objeto Usuario ao request
        request.setCriador(criador);

        // --- O restante da sua lógica de criação continua, mas simplificada ---

        // Busca do executor (opcional) - Mantenha sua lógica original aqui
        Usuario executor = null;
        if (request.getExecutor() != null) {
            // ... (sua lógica para buscar o executor) ...
            // Note que você precisará usar o ID que está no request.getExecutor().getId()
        }

        // Se você estiver usando o construtor @Builder, ajuste a lógica:
        OrdemServico nova = OrdemServico.builder()
                .data_criacao(LocalDateTime.now())
                .descricao(request.getDescricao())
                .titulo(request.getTitulo())
                .categoria(request.getCategoria())
                .prioridade(request.getPrioridade())
                .status(StatusOS.ABERTO)
                .criador(criador) // <--- Garante que o criador está aqui
                .executor(executor)
                .build();

        return repository.save(nova);
    }

    // --- ATUALIZAR ---
    public OrdemServico atualizar(int id, OrdemServico request) {
        OrdemServico existente = buscarPorId(id);

        StatusOS statusAntigo = existente.getStatus();

        // Atualiza campos normais
        existente.setDescricao(request.getDescricao());
        existente.setTitulo(request.getTitulo());
        existente.setCategoria(request.getCategoria());
        existente.setPrioridade(request.getPrioridade());

        // Atualiza CRIADOR (obrigatório)
        Usuario criador = usuarioRepository.findById(request.getCriador().getId())
                .orElseThrow(() -> new RuntimeException("Criador não encontrado"));
        existente.setCriador(criador);

        // Atualiza EXECUTOR (opcional)
        if (request.getExecutor() != null) {
            Usuario executor = usuarioRepository.findById(request.getExecutor().getId())
                    .orElseThrow(() -> new RuntimeException("Executor não encontrado"));
            existente.setExecutor(executor);
        }

        // Atualiza status caso tenha vindo na requisição
        if (request.getStatus() != null) {
            existente.setStatus(request.getStatus());
        }

        OrdemServico salva = repository.save(existente);

        // Dispara evento SOMENTE SE o status mudou
        if (salva.getStatus() != statusAntigo) {
            eventPublisher.publishEvent(new OrdemServicoStatusAtualizadoEvent(salva));
        }

        return salva;
    }

    // --- LISTAR ---
    public List<OrdemServico> listar() {
        return repository.findAll();
    }

    // --- BUSCAR POR ID ---
    public OrdemServico buscarPorId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));
    }

    // --- DELETAR ---
    public void deletar(int osId, int executorId) {

        // 1. VERIFICAÇÃO DE AUTORIZAÇÃO MANUAL
        Usuario executor = userService.buscarPorId(executorId);

        // Regra: Somente o EXECUTOR pode deletar
        if (executor.getRole() != UserRoles.EXECUTOR) {
            throw new RuntimeException("Acesso Negado. Somente o EXECUTOR pode deletar uma OS.");
        }
        if (repository.getById(osId).getStatus() != StatusOS.CONCLUIDO){
            throw new RuntimeException("A Ordem de serviço deve está concluida para ser deletada.");
        }


        repository.deleteById(osId);
    }

    public OrdemServico avancarStatus(Integer osId, Integer executorId) {

        // 1. VERIFICAÇÃO DE AUTORIZAÇÃO MANUAL
        Usuario executor = userService.buscarPorId(executorId);

        // Verifica se o usuário tem a role EXECUTOR
        if (executor.getRole() != UserRoles.EXECUTOR) {
            // Lança uma exceção de acesso negado ou regra de negócio violada
            throw new RuntimeException("Acesso Negado. Somente o EXECUTOR pode avançar o status da OS.");
        }

        // 2. LÓGICA DE NEGÓCIO (Avançar Status)
        OrdemServico os = buscarPorId(osId);
        StatusOS statusAtual = os.getStatus();

        StatusOS proximoStatus;
        try {
            int proximoOrdinal = statusAtual.ordinal() + 1;
            proximoStatus = StatusOS.values()[proximoOrdinal];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("A Ordem de Serviço já está no último status possível.");
        }

        // 3. Atualiza e Salva
        os.setStatus(proximoStatus);
        return repository.save(os);
    }
}
