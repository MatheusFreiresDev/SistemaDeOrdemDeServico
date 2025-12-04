package com.ordemDeServico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OrdemDeServicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdemDeServicoApplication.class, args);
	}

}
