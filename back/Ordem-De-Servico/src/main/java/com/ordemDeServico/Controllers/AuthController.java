package com.ordemDeServico.Controllers;

import com.ordemDeServico.DTOS.LoginRequest;
import com.ordemDeServico.DTOS.RegisterRequest;
import com.ordemDeServico.Repository.UsuarioRepository;
import com.ordemDeServico.Service.TokenService;
import com.ordemDeServico.Service.UserService;
import com.ordemDeServico.model.Usuario;
import com.ordemDeServico.model.enums.UserRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints responsáveis pelo Login e Registro de novos usuários.")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final UserService userService;

    @Operation(summary = "Realizar Login", description = "Autentica um usuário e retorna um Token JWT para acesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso. Retorna o Token."),
            @ApiResponse(responseCode = "403", description = "Erro de autenticação (Senha incorreta ou usuário inexistente).")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        if(usuarioRepository.findByEmail(loginRequest.email()).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email incorreto.");
        }
        try {
            var userToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha());
            authenticationManager.authenticate(userToken);
            Usuario usuario = usuarioRepository.findByEmail(loginRequest.email()).orElseThrow();
            String token = tokenService.GenerateToken(usuario);
            return ResponseEntity.ok("Logado Com Sucesso. " + token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Falha na autenticação: Credenciais inválidas.");
        }
    }

    @Operation(summary = "Registrar novo Cliente", description = "Cria uma nova conta de usuário com perfil CLIENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro na requisição ou Email já cadastrado.")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Usuario newUser = Usuario.builder()
                .email(request.email())
                .senha(request.senha())
                .role(UserRoles.CLIENTE)
                .nome(request.nome())
                 .ordensExecutadas(new ArrayList<>())
                .ordensCriadas(new ArrayList<>())
                .build();

        userService.criar(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(request.nome() + ", obrigado por se juntar com a gente!");
    }
}