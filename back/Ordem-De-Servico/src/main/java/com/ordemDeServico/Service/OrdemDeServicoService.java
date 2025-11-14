package com.ordemDeServico.Service;

import com.ordemDeServico.Repository.OrdemServicoRepository;
import com.ordemDeServico.model.OrdemServico;
import com.ordemDeServico.model.enums.StatusOS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemDeServicoService {

    private final OrdemServicoRepository repository;

    public OrdemServico criar(OrdemServico request) {
        OrdemServico nova = OrdemServico.builder()
                .descricao(request.getDescricao())
                .prioridade(request.getPrioridade())
                .status(StatusOS.ABERTO)
                .criador(request.getCriador())
                .executor(request.getExecutor())
                .build();

        return repository.save(nova);
    }

    public List<OrdemServico> listar() {
        return repository.findAll();
    }

    public OrdemServico buscarPorId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));
    }

    public OrdemServico atualizar(int id, OrdemServico request) {
        OrdemServico existente = buscarPorId(id);

        existente.setDescricao(request.getDescricao());
        existente.setPrioridade(request.getPrioridade());
        existente.setCriador(request.getCriador());
        existente.setExecutor(request.getExecutor());

        // status só muda se o request mandar algo
        if (request.getStatus() != null) {
            existente.setStatus(request.getStatus());
        }

        return repository.save(existente);
    }

    public void deletar(int id) {
        OrdemServico existente = buscarPorId(id);
        repository.delete(existente);
    }
}
