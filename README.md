# ComandaNet

Sistema web de gerenciamento de produtos e comandas, desenvolvido com Java 17, Spring Boot, Spring Data JPA e MySQL.

## Requisitos
- JDK 17 ou superior
- MySQL 8+
- Maven (ou use `mvnw.cmd` no Windows)

## Configurar o MySQL
1. Abra o MySQL Workbench ou DBeaver.
2. Execute o script `database/criar_banco.sql`.
3. Confira as credenciais em `src/main/resources/application.properties`.
   - Padrão: usuário `root`, senha vazia.
   - É possível configurar `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` como variáveis de ambiente.
4. Execute a aplicação.

O Hibernate cria/atualiza as tabelas `usuarios`, `produtos`, `comandas` e `itens_comanda` automaticamente.

## Executar
No Windows:
```bat
mvnw.cmd spring-boot:run
```
Ou:
```bat
mvn spring-boot:run
```
Acesse http://localhost:8080.

## Primeiro acesso
Quando a tabela `usuarios` estiver vazia, a aplicação cria duas contas iniciais:
- Administrador: `admin` / `Admin@12345`
- Usuário comum: `usuario` / `Usuario@12345`

Altere essas senhas depois do primeiro acesso. As senhas são armazenadas com hash PBKDF2 e salt aleatório; não são gravadas em texto puro.

## Permissões
- ADM: cadastra/altera/exclui produtos, gerencia usuários e pode editar, renomear e fechar comandas.
- UC: consulta produtos e emite comandas. Não pode cadastrar produtos, gerenciar usuários nem editar/fechar comandas.

As permissões são verificadas no backend por sessão HTTP; ocultar botões no navegador é apenas um complemento visual.

## API de autenticação
- `POST /auth/login` — recebe `{"username":"admin","senha":"Admin@12345"}`
- `GET /auth/me` — retorna o usuário autenticado
- `POST /auth/logout` — encerra a sessão

## API de usuários (somente ADM)
- `GET /usuarios` — lista usuários sem expor hashes de senha
- `POST /usuarios` — cria usuário. Exemplo:
```json
{
  "username": "funcionario",
  "senha": "SenhaForte123",
  "perfil": "UC"
}
```
Os perfis aceitos são `ADM` e `UC`.

## Persistência
Usuários, produtos, comandas e itens das comandas são persistidos no MySQL. O perfil `h2` pode ser usado para desenvolvimento local sem MySQL:
```bat
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```
