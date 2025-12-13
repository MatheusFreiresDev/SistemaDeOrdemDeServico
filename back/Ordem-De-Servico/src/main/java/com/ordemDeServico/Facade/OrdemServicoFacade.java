package com.ordemDeServico.Facade;

import com.ordemDeServico.Service.OrdemDeServicoService;
import com.ordemDeServico.Service.UserService;
import com.ordemDeServico.model.OrdemServico;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrdemServicoFacade {

    private final UserService userService;
    private final OrdemDeServicoService ordemService;

    // OrdemServicoFacade.java (Exemplo)

    public OrdemServico criarOS(OrdemServico request) {
        return ordemService.criar(request);
    }

    public List<OrdemServico> listarOS() {
        return ordemService.listar();
    }

    public OrdemServico buscarOSPorId(int id) {
        return ordemService.buscarPorId(id);
    }

    public OrdemServico atualizarOS( int id, OrdemServico request) {
        return ordemService.atualizar(id,request);
    }

    public void deletarOS(int idDaOs) {
        // Repassa a responsabilidade de verificação e deleção para o Service
        ordemService.deletar(idDaOs);
    }

    public OrdemServico avancarStatusOS(Integer id) {
        // Repassa a responsabilidade de verificar a Role e avançar o status
        return ordemService.avancarStatus(id);
    }
}
