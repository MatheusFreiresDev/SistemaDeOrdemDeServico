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

    public OrdemServico criarOS(String criador, OrdemServico request) {
        var user = userService.buscarPorNome(criador);
        if (user == null) throw new RuntimeException("Usuário inválido");

        return ordemService.criar(request);
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

    public void deletarOS(int id) {
        ordemService.deletar(id);
    }
}
