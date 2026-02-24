# 🛠️ Sistema de Gestão de Ordens de Serviço

API RESTful para gerenciamento de Ordens de Serviço com autenticação JWT, controle de acesso por perfis e notificações automáticas por e-mail.

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.3.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Security-JWT-red?style=for-the-badge&logo=springsecurity&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-Flyway-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>
</p>

---

## 🔗 Links

| | |
|---|---|
| 🌐 **Frontend** | `[em breve]` |
| 📄 **Swagger UI** | `[em breve]` |
| 💻 **Repositório** | [github.com/MatheusFreiresDev/SistemaDeOrdemDeServico](https://github.com/MatheusFreiresDev/SistemaDeOrdemDeServico) |

---

## 📌 Sobre o Projeto

O projeto consiste em uma **API RESTful** completa para o gerenciamento do ciclo de vida de Ordens de Serviço, com foco em boas práticas de engenharia de software.

O sistema possui três perfis de usuário com permissões distintas:

- **CLIENTE** — abre e acompanha suas próprias OS
- **EXECUTOR** — visualiza, aceita e avança o status das OS
- **ADMIN** — acesso total ao sistema

---

## 🚀 Tecnologias

### Backend
| Tecnologia | Uso |
|---|---|
| Java 17 + Spring Boot 3.3.6 | Core da aplicação |
| Spring Security + JWT | Autenticação stateless |
| Spring Data JPA + Hibernate | Persistência |
| MySQL | Banco de dados |
| Flyway | Versionamento de schema |
| Springdoc OpenAPI (Swagger) | Documentação interativa |
| Spring Mail | Notificações por e-mail |
| Lombok | Redução de boilerplate |

### Frontend
| Tecnologia | Uso |
|---|---|
| HTML5, CSS3, JavaScript (Vanilla) | Interface web |

> O frontend é uma prova de conceito para validar o consumo da API, fluxo de autenticação JWT e tratamento de CORS.

---

## ⚙️ Funcionalidades

### 🔐 Autenticação
- Registro e login com criptografia BCrypt
- Geração e validação de tokens JWT
- Proteção de rotas via filtro de segurança customizado

### 📋 Ordens de Serviço
- Criação de OS com título, descrição, prioridade e categoria
- Fluxo de status: `ABERTO` → `EM_EXECUCAO` → `CONCLUIDO`
- Listagem filtrada por perfil (cada usuário vê apenas o que lhe compete)
- Edição e exclusão com validação de permissão

### 📧 Notificações
- E-mail automático ao cliente sempre que o status da sua OS é alterado
- Implementação desacoplada com **Events e Listeners** do Spring (padrão Observer)

### 📄 Documentação
- API 100% documentada e testável via Swagger UI

---

## 🗂️ Estrutura do Projeto

```
Ordem-De-Servico/
├── src/main/java/com/ordemDeServico/
│   ├── configSecurity/     # JWT, filtros e configuração de segurança
│   ├── controllers/        # Endpoints da API
│   ├── dtos/               # Objetos de entrada e saída
│   ├── exceptions/         # Exceções customizadas
│   ├── facade/             # Camada de orquestração
│   ├── model/              # Entidades JPA e enums
│   ├── repository/         # Acesso ao banco (Spring Data JPA)
│   └── service/            # Lógica de negócio e eventos
│       ├── event/          # Eventos de domínio
│       └── listeners/      # Listeners para notificações
└── src/main/resources/
    ├── db/migration/       # Scripts Flyway
    └── application.yaml    # Configuração da aplicação
```

---

## 🔌 Endpoints Principais

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| `POST` | `/auth/register` | Registrar novo usuário | ❌ |
| `POST` | `/auth/login` | Login e geração do JWT | ❌ |
| `GET` | `/os` | Listar OS (filtrado por perfil) | ✅ |
| `POST` | `/os` | Criar nova OS | ✅ |
| `PUT` | `/os/{id}` | Atualizar OS | ✅ |
| `PUT` | `/os/{id}/avancar-status` | Avançar status da OS | ✅ Executor |
| `DELETE` | `/os/{id}` | Deletar OS | ✅ |

> Documentação completa e testável no **Swagger UI** após subir a aplicação.

---

## 🖥️ Como Rodar Localmente

### Pré-requisitos
- Java 17+
- Maven
- MySQL

### Configuração

1. Clone o repositório:
```bash
git clone https://github.com/MatheusFreiresDev/SistemaDeOrdemDeServico.git
```

2. Configure o `application.yaml` com suas credenciais do MySQL:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ordemservico
    username: seu_usuario
    password: sua_senha
  jpa:
    hibernate:
      ddl-auto: validate

api:
  security:
    token:
      secret: seu_secret_jwt
```

3. Crie o banco de dados:
```sql
CREATE DATABASE ordemservico;
```

4. Suba a aplicação — o Flyway criará as tabelas automaticamente:
```bash
./mvnw spring-boot:run
```

5. Acesse o Swagger:
```
http://localhost:8080/swagger-ui.html
```

### Frontend

Navegue até a pasta `front/` e abra o `login.html` direto no navegador. Não requer instalação.

---

## 🧪 Testes

```bash
./mvnw test
```

---

## 👨‍💻 Autor

**Matheus Freires**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](www.linkedin.com/in/matheus-freires-pereira-a74580303)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/MatheusFreiresDev)

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
