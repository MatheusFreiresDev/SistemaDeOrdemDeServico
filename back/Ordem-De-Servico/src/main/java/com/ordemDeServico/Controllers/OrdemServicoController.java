package com.ordemDeServico.Controllers;

import com.ordemDeServico.Facade.OrdemServicoFacade;
import com.ordemDeServico.Service.OrdemDeServicoService;
import com.ordemDeServico.model.OrdemServico;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/os")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoFacade facade;

    @PostMapping("/{criador}")
    public ResponseEntity<OrdemServico> criar(
            @PathVariable String criador, // <--- Continua pegando da URL
            @RequestBody OrdemServico request) {

        return ResponseEntity.status(201).body(facade.criarOS(criador, request)); // Use 201 Created
    }

    @GetMapping
    public ResponseEntity<List<OrdemServico>> listar() {
        return ResponseEntity.ok(facade.listarOS());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServico> buscar(
            @PathVariable int id) {

        return ResponseEntity.ok(facade.buscarOSPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServico> atualizar(
            @PathVariable int id,
            @RequestBody OrdemServico    request) {

        return ResponseEntity.ok(facade.atualizarOS(id, request));
    }

    @DeleteMapping("/{id}/{executorId}") // Adiciona o ID do usuário que deleta
    public ResponseEntity<Void> deletar(
            @PathVariable int id, // ID da OS a ser deletada
            @PathVariable int executorId) { // ID do usuário executor

        facade.deletarOS(id, executorId); // Atualiza a chamada na facade
        return ResponseEntity.noContent().build();
    }
        @PutMapping("/{id}/avancar-status/{executorId}") // Novo endpoint com o ID do Executor
        public ResponseEntity<OrdemServico> avancarStatus(
                @PathVariable int id, // ID da OS
                @PathVariable int executorId) { // ID do usuário que tenta executar a ação

            OrdemServico osAtualizada = facade.avancarStatusOS(id, executorId);

            return ResponseEntity.ok(osAtualizada);
        }
}
