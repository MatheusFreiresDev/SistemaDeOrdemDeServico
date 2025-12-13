# 🛠️ Sistema de Gestão de Ordens de Serviço (API RESTful)

Este projeto consiste no desenvolvimento de uma **API RESTful robusta** para o gerenciamento de Ordens de Serviço, focada em boas práticas de engenharia de software, segurança com Spring Security e arquitetura em camadas.

O projeto inclui também uma interface **Frontend leve (Vanilla JS)** desenvolvida exclusivamente para demonstrar o consumo dos endpoints, o tratamento de CORS e o fluxo de autenticação via Token JWT.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.6-green)
![Security](https://img.shields.io/badge/Spring_Security-JWT-red)
![Database](https://img.shields.io/badge/MySQL-Flyway-blue)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-brightgreen)

## 🚀 Tecnologias Utilizadas

### Backend (Core)
* **Linguagem:** Java 17
* **Framework:** Spring Boot 3.3.6
* **Segurança:** Spring Security + JWT (JSON Web Token) para autenticação Stateless.
* **Persistência:** Spring Data JPA & Hibernate.
* **Banco de Dados:** MySQL.
* **Migração de Dados:** Flyway (Versionamento de Schema).
* **Documentação:** Springdoc OpenAPI (Swagger UI).
* **E-mail:** Spring Mail (Envio de notificações automáticas).
* **Arquitetura:** DTOs, Service Layer, Repository Pattern, Event Listeners (Observer).

### Frontend (Cliente de Consumo)
* **Tecnologias:** HTML5, CSS3 e JavaScript (Vanilla).
* **Objetivo:** Prova de conceito (PoC) para validar a integração com a API, configurações de CORS e passagem de Bearer Tokens nos headers.

---

## ⚙️ Funcionalidades Principais

1.  **Autenticação e Segurança (JWT):**
    * Registro e Login de usuários com criptografia de senha (BCrypt).
    * Controle de acesso baseado em funções (Roles: CLIENTE, EXECUTOR, ADMIN).
    * Proteção de rotas via Filtro de Segurança personalizado.

2.  **Gestão de Ordens de Serviço (CRUD):**
    * Abertura de novas OS com definição de prioridade e categoria.
    * Atualização de status (ABERTO -> EM EXECUÇÃO -> CONCLUÍDO).
    * Listagem, busca e exclusão de ordens.

3.  **Sistema de Notificações:**
    * Envio automático de e-mails para o cliente sempre que o status da sua OS é alterado.
    * Implementação desacoplada utilizando Eventos e Listeners do Spring.

4.  **Documentação Interativa:**
    * API 100% documentada e testável via Swagger UI.

---

## 📖 Documentação da API (Swagger)

Com a aplicação rodando, acesse a documentação completa em:

👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Aqui você poderá ver os schemas JSON de entrada e saída e testar as requisições em tempo real.

---

## 🖥️ Como testar o Frontend

O frontend não requer instalação de pacotes (npm/yarn), pois foi feito em JS puro.

1.  Navegue até a pasta `front` do projeto.
2.  Abra o arquivo `index.html` (ou `login.html`) diretamente no seu navegador.
3.  **Fluxo de Teste:**
    * Crie uma conta na tela de registro.
    * Faça login (o Token JWT será salvo no `localStorage`).
    * Você será redirecionado para o painel principal onde poderá interagir com a API.

---

## 📂 Estrutura do Projeto
```text
Ordem-De-Servico
├── src
│   ├── main
│   │   ├── java/com/ordemDeServico
│   │   │   ├── ConfigSecurity  # Segurança (JWT/Filtros)
│   │   │   ├── Controllers     # Endpoints da API
│   │   │   ├── DTOS            # Dados de Entrada/Saída
│   │   │   ├── Exceptions      # Tratamento de Erros
│   │   │   ├── model           # Entidades do Banco
│   │   │   ├── Repository      # Acesso ao Banco (JPA)
│   │   │   └── Service         # Lógica de Negócio
│   │   └── resources
│   │       ├── db/migration    # Scripts do Flyway
│   │       └── application.yml # Configuração
│```
└── front                       # Cliente Web (Teste)

```

## 🤝 Contribuição

Sugestões e melhorias são bem-vindas! Sinta-se à vontade para abrir uma *issue* ou enviar um *pull request*.

## 📄 Licença

Este projeto está sob a licença MIT.
