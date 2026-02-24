package com.ordemDeServico.configSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Classes de CORS corretas (Servlet Stack)
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // CLASSE CORRETA

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Injeção do filtro de segurança (Atenção: verifique se o StackOverflow foi resolvido injetando via construtor ou no método SecurityFilterChain)
    @Autowired
    SecurityFilter securityFilter;

    // --- CORREÇÃO DE CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permite todas as origens (Desenvolvimento)
        configuration.setAllowedOrigins(List.of("*"));

        // Permite os métodos essenciais: POST, GET, OPTIONS, DELETE, PUT
        configuration.setAllowedMethods(List.of("POST", "GET", "OPTIONS", "DELETE", "PUT"));

        // Permite todos os cabeçalhos (incluindo Authorization para o JWT)
        configuration.setAllowedHeaders(List.of("*"));

        // Usamos a CLASSE CORRETA do pacote org.springframework.web.cors
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Aplica esta configuração a todos os endpoints
        source.registerCorsConfiguration("/**", configuration);

        // Não é necessário o cast explícito se a classe correta for importada
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests( authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}