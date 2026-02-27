package com.ordemDeServico.controllers;

import com.ordemDeServico.facade.OrdemServicoFacade;
import com.ordemDeServico.model.OrdemServico;
import com.ordemDeServico.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/os")
@RequiredArgsConstructor
@Tag(name = "Gestão de Ordens de Serviço", description = "Endpoints para criar, visualizar, editar e gerenciar o ciclo de vida das OS.")
public class OrdemServicoController {
    OrdemServicoFacade facade;
    @Operation(summary = "Listar todas as OS", description = "Retorna a lista de OS baseada no perfil: ADMIN vê tudo, CLIENTE vê as suas, EXECUTOR vê as suas + as disponíveis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.")
    })
    @GetMapping
    public ResponseEntity<List<OrdemServico>> listar() {
        return ResponseEntity.ok(facade.listarOS());
    }

    @Operation(summary = "Buscar OS por ID", description = "Retorna os detalhes de uma OS específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OS encontrada."),
            @ApiResponse(responseCode = "404", description = "OS não encontrada."),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Você não é o dono ou executor desta OS).")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServico> buscar(@PathVariable int id) {
        return ResponseEntity.ok(facade.buscarOSPorId(id));
    }

    @Operation(summary = "Atualizar OS", description = "Atualiza dados da OS. Clientes só podem editar se a OS ainda estiver ABERTA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OS atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada (ex: tentar editar OS já finalizada)."),
            @ApiResponse(responseCode = "404", description = "OS não encontrada.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@RequestBody OrdemServico ordemServico, @PathVariable int id) {
        return ResponseEntity.ok(facade.atualizarOS(id, ordemServico));
    }

    @Operation(summary = "Deletar OS", description = "Remove uma OS do sistema. Requer permissão adequada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "OS deletada com sucesso (Sem conteúdo)."),
            @ApiResponse(responseCode = "403", description = "Acesso negado."),
            @ApiResponse(responseCode = "404", description = "OS não encontrada.")
    })
    @DeleteMapping("/{idDaOs}")
    public ResponseEntity<Void> deletar(@PathVariable int idDaOs) {
        facade.deletarOS(idDaOs);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Avançar Status da OS", description = "Permite ao EXECUTOR avançar o status (ABERTO -> EM EXECUÇÃO -> CONCLUÍDO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status avançado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Não é possível avançar (ex: já está CONCLUÍDO)."),
            @ApiResponse(responseCode = "403", description = "Apenas Executores podem realizar esta ação.")
    })
    @PutMapping("/{id}/avancar-status")
    public ResponseEntity<OrdemServico> avancarStatus(@PathVariable int id) {
        OrdemServico osAtualizada = facade.avancarStatusOS(id);
        return ResponseEntity.ok(osAtualizada);
    }
}