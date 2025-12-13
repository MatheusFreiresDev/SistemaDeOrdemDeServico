package com.ordemDeServico.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ordemDeServico.model.enums.UserRoles;
import io.swagger.v3.oas.annotations.media.Schema; // Importação necessária!
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
@Schema(description = "Representa um usuário do sistema (Cliente, Executor, ou Admin) e suas credenciais de segurança.") // Documentação da classe
public class Usuario implements UserDetails {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do usuário.", example = "1")
    private int id;

    @Schema(description = "Nome completo do usuário.", example = "Matheus Freires")
    private String nome;

    @Schema(description = "E-mail do usuário, usado como nome de usuário para login.", example = "matheus@empresa.com.br")
    private String email;

    @JsonIgnore // A senha não deve ser exposta na resposta da API
    @Schema(description = "Senha criptografada (não é retornada na API).", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Perfil de acesso do usuário.", example = "CLIENTE", allowableValues = {"CLIENTE", "EXECUTOR", "ADMIN"})
    private UserRoles role;

    @JsonIgnore
    @OneToMany(mappedBy = "criador")
    @Schema(description = "Lista de Ordens de Serviço criadas por este usuário.", accessMode = Schema.AccessMode.READ_ONLY)
    private List<OrdemServico> ordensCriadas;

    @JsonIgnore
    @OneToMany(mappedBy = "executor")
    @Schema(description = "Lista de Ordens de Serviço atribuídas a este usuário para execução.", accessMode = Schema.AccessMode.READ_ONLY)
    private List<OrdemServico> ordensExecutadas;

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email + "'}";
    }
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if( this.role == UserRoles.EXECUTOR) {
            return List.of(new SimpleGrantedAuthority("ROLE_EXECUTOR"),new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    public UserRoles getAuthority() {
        return role;
    }
    @JsonIgnore
    @Override
    public String getPassword() {
        return this.senha;
    }
    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    // Métodos de UserDetails mantidos (sem documentação Swagger, pois são internos ao Spring Security)

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}