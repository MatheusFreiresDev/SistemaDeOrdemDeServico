package com.ordemDeServico.Controllers;

import com.ordemDeServico.Repository.UserRepository;
import com.ordemDeServico.model.User;
import com.ordemDeServico.model.enums.UserRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserRepository userRepository;


    @GetMapping
    public ResponseEntity<?> teste () {
        User teste = User.builder().nome("teste").email("teste").senha("teste").role(UserRoles.CLIENTE).ordensExecutadas(new ArrayList<>()).ordensCriadas(new ArrayList<>()).build();
        userRepository.save(teste);
        return ResponseEntity.ok(userRepository.findByNome(teste.getNome()).orElse(null));
    }
}
