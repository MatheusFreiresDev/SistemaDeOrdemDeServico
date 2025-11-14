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
            @PathVariable String criador,
            @RequestBody OrdemServico request) {

        return ResponseEntity.ok(facade.criarOS(criador, request));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) {
        facade.deletarOS(id);
        return ResponseEntity.noContent().build();
    }
}
