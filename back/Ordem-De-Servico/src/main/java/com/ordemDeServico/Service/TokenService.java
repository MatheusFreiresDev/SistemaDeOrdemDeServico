package com.ordemDeServico.Service;

import com.auth0.jwt.JWT;
import com.ordemDeServico.Exceptions.JWTCreationException;
import com.ordemDeServico.model.Usuario;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String code;

    public String GenerateToken(Usuario user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(code);
            return JWT.create()
                    .withIssuer("api-OS")
                    .withSubject(user.getUsername())
                    .withClaim("userId", user.getId()) // <--- Adicione esta linha
                    .withClaim("role", user.getRole().toString())
                    .withClaim("nome", user.getNome())
                    .withExpiresAt(tokenExperirationDate())
                    .sign(algorithm);
        }catch (JWTCreationException e) {
            return "Erro na criacao JWT";
        }
    }

    private Instant tokenExperirationDate() {
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"));
    }

    public String ValidateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(code);
        return JWT.require(algorithm)
                .withIssuer("api-OS")
                .build()
                .verify(token)
                .getSubject();
    }
}
