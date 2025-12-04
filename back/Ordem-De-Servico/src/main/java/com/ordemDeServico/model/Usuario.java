package com.ordemDeServico.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ordemDeServico.model.enums.UserRoles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private String nome;
    private String email;
    private String senha;
    @Enumerated(EnumType.STRING)
    private UserRoles role;
    @JsonIgnore
    @OneToMany(mappedBy = "criador")
    private List<OrdemServico> ordensCriadas;

    @JsonIgnore
    @OneToMany(mappedBy = "executor")
    private List<OrdemServico> ordensExecutadas;
    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email + "'}";
    }

}
