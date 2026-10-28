# ComandaNet

Sistema web para gerenciamento de comandas de restaurantes, desenvolvido em Java com Spring Boot. O projeto reúne uma interface web e uma API no back-end para organizar comandas e produtos.

## Funcionalidades

- Criar e consultar comandas.
- Adicionar itens/produtos às comandas.
- Consultar produtos e gerenciar seu cadastro conforme as operações disponíveis na aplicação.
- Interface web integrada ao back-end.
- Tratamento centralizado de exceções.

> **Observação:** os dados estão configurados para serem armazenados em um banco H2 em memória. Por padrão, eles são perdidos quando a aplicação é encerrada.

## Tecnologias utilizadas

- **Java 18**
- **Spring Boot 4.1.1**
- **Spring Web MVC** — endpoints HTTP e aplicação web
- **Spring Data JPA / Hibernate** — persistência e mapeamento de entidades
- **H2 Database** — banco de dados em memória
- **Maven** — gerenciamento de dependências e execução
- **HTML, CSS e JavaScript** — interface do usuário
- **Lombok** — redução de código repetitivo

## Pré-requisitos

- JDK 18 instalado e configurado.
- IDE compatível com Java, como IntelliJ IDEA, ou terminal.
- Conexão com a internet na primeira execução para baixar as dependências Maven.

## Como executar

### Pelo IntelliJ IDEA

1. Extraia o arquivo do projeto.
2. Abra a pasta `projeto-comandas-restaurante` no IntelliJ IDEA.
3. Aguarde o Maven carregar e baixar as dependências.
4. Configure o projeto para usar o **JDK 18**.
5. Execute a classe `com.example.demo.DemoApplication`.
6. Abra no navegador: <http://localhost:8080>.

### Pelo terminal

Na pasta raiz do projeto, execute:

**Windows (PowerShell ou Prompt de Comando):**

```bat
mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

Se o Maven Wrapper não conseguir baixar o Maven, instale/configure o Maven e execute `mvn spring-boot:run` na pasta do projeto.

## Banco de dados H2

A configuração está em `src/main/resources/application.properties`.

- URL JDBC: `jdbc:h2:mem:tarefasdb`
- Usuário: `sa`
- Senha: vazia
- Console H2: <http://localhost:8080/h2-console>

No console H2, utilize a URL JDBC acima e as credenciais indicadas. Como o banco é em memória, ele existe apenas enquanto a aplicação está em execução.

## Estrutura principal

```text
src/
└── main/
    ├── java/com/example/demo/
    │   ├── controller/   # Controladores e endpoints HTTP
    │   ├── dto/          # Objetos de transferência de dados
    │   ├── exception/    # Tratamento de exceções
    │   ├── model/        # Entidades e modelos
    │   ├── repository/   # Acesso e armazenamento de dados
    │   ├── service/      # Regras de negócio
    │   └── DemoApplication.java
    └── resources/
        ├── static/       # HTML, CSS e JavaScript da interface
        └── application.properties
```

## Endpoints

Uma coleção de exemplos de requisições HTTP está disponível em `http/endpoints.http`. Consulte os controladores em `src/main/java/com/example/demo/controller/` para verificar as rotas e os métodos disponíveis na versão atual.

## Desenvolvimento

1. Implemente alterações nas camadas correspondentes (`controller`, `service`, `repository`, `model` e `dto`).
2. Atualize a interface em `src/main/resources/static/` quando necessário.
3. Execute a aplicação e teste as operações pela interface ou pelo arquivo `http/endpoints.http`.

## Licença

Nenhuma licença foi especificada neste repositório.
