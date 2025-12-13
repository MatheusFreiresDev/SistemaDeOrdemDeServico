package com.ordemDeServico.Controllers;

import com.ordemDeServico.Service.UserService;
import com.ordemDeServico.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Gestão de Usuários", description = "Endpoints para administração de usuários (CRUD completo).")
public class UserController {

    private final UserService service;

    @Operation(summary = "Criar Usuário", description = "Cria um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.")
    })
    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario request) {
        return ResponseEntity.ok(service.criar(request));
    }

    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista com todos os usuários cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.")
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar Usuário por ID", description = "Retorna os detalhes de um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualizar Usuário", description = "Atualiza os dados de um usuário existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable int id, @RequestBody Usuario request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @Operation(summary = "Deletar Usuário", description = "Remove um usuário do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso (Sem conteúdo)."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}