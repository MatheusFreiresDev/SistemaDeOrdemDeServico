package com.ordemDeServico.Facade;

import com.ordemDeServico.Service.OrdemDeServicoService;
import com.ordemDeServico.Service.UserService;
import com.ordemDeServico.model.OrdemServico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrdemServicoFacade {

    private final UserService userService;
    private final OrdemDeServicoService ordemService;

    // OrdemServicoFacade.java (Exemplo)

    public OrdemServico criarOS(String criadorId, OrdemServico request) {
        // 🚨 MUDANÇA: Repassa o ID do criador para o Service
        return ordemService.criar(criadorId, request);
    }

    public List<OrdemServico> listarOS() {
        return ordemService.listar();
    }

    public OrdemServico buscarOSPorId(int id) {
        return ordemService.buscarPorId(id);
    }

    public OrdemServico atualizarOS(int id, OrdemServico request) {
        return ordemService.atualizar(id, request);
    }

    public void deletarOS(int id, int executorId) {
        // Repassa a responsabilidade de verificação e deleção para o Service
        ordemService.deletar(id, executorId);
    }

    public OrdemServico avancarStatusOS(Integer id, Integer executorId) {
        // Repassa a responsabilidade de verificar a Role e avançar o status
        return ordemService.avancarStatus(id, executorId);
    }
}
