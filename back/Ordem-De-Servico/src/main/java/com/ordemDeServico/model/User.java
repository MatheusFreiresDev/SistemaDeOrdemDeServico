package com.ordemDeServico.model;

import com.ordemDeServico.model.enums.UserRoles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Collection;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "usuarios")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private String nome;
    private String email;
    private String senha;
    @Enumerated(EnumType.STRING)
    private UserRoles role;
    @OneToMany(mappedBy = "criador")
    private List<OrdemDeServico> ordensCriadas;

    @OneToMany(mappedBy = "executor")
    private List<OrdemDeServico> ordensExecutadas;

}
