package com.ordemDeServico.Service;

import com.ordemDeServico.Service.Event.OrdemServicoStatusAtualizadoEvent;
import com.ordemDeServico.Repository.OrdemServicoRepository;
import com.ordemDeServico.Repository.UsuarioRepository;
import com.ordemDeServico.model.OrdemServico;
import com.ordemDeServico.model.Usuario;
import com.ordemDeServico.model.enums.StatusOS;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemDeServicoService {

    private final OrdemServicoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    // --- CRIAR ---
    public OrdemServico criar(OrdemServico request) {

        // Busca completa do criador
        Usuario criador = usuarioRepository.findById(request.getCriador().getId())
                .orElseThrow(() -> new RuntimeException("Criador não encontrado"));

        // Busca do executor (opcional)
        Usuario executor = null;
        if (request.getExecutor() != null) {
            executor = usuarioRepository.findById(request.getExecutor().getId())
                    .orElseThrow(() -> new RuntimeException("Executor não encontrado"));
        }

        OrdemServico nova = OrdemServico.builder()
                .data_criacao(LocalDateTime.now())
                .descricao(request.getDescricao())
                .titulo(request.getTitulo())
                .categoria(request.getCategoria())
                .prioridade(request.getPrioridade())
                .status(StatusOS.ABERTO)
                .criador(criador)
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
    public void deletar(int id) {
        OrdemServico existente = buscarPorId(id);
        repository.delete(existente);
    }
}
