# ComandaNet

Sistema web acadêmico para controle de produtos e emissão de comandas de restaurante, desenvolvido com Java 17, Spring Boot e interface HTML/CSS/JavaScript.

## Funcionalidades

- Tela de login antes de acessar o sistema.
- Perfil **ADM**: pode consultar e cadastrar produtos, emitir comandas e renomear, editar ou fechar comandas abertas.
- Perfil **UC** (usuário comum): pode consultar produtos e emitir/consultar comandas, mas não pode alterar produtos nem editar/fechar comandas existentes.
- Comandas com nome personalizado (ex.: Mesa 04 ou nome do cliente).
- Edição do nome e dos produtos de comandas abertas (ADM).
- Fechamento de comandas, com bloqueio de novas edições após o fechamento (ADM).
- Validação e ajuste do estoque ao emitir ou editar uma comanda.
- Cálculo automático dos subtotais e total da comanda.
- Sessão de login e botão para sair.

## Contas iniciais para teste

| Perfil | Usuário | Senha |
|---|---|---|
| Administrador (ADM) | `admin` | `Admin@12345` |
| Usuário comum (UC) | `usuario` | `Usuario@12345` |

**Importante:** essas são credenciais iniciais de demonstração. Altere-as antes de utilizar o sistema em um ambiente real.

## Como executar no IntelliJ IDEA

1. Extraia o arquivo ZIP.
2. Abra a pasta `comanda-net` no IntelliJ IDEA como projeto Maven.
3. Configure o projeto para usar o JDK 17 (ou superior compatível).
4. Aguarde o Maven baixar as dependências.
5. Execute a classe `com.example.demo.DemoApplication`.
6. Acesse [http://localhost:8080](http://localhost:8080) no navegador.

Também é possível executar no terminal, na pasta do projeto:

- Windows: `mvnw.cmd spring-boot:run`
- Linux/macOS: `./mvnw spring-boot:run`

## Observações

- Os produtos e comandas ficam em repositórios em memória e são apagados quando a aplicação é encerrada.
- As comandas são registros internos do restaurante; o sistema não emite documento fiscal autorizado pela SEFAZ.
- O controle de permissões é validado no backend, além de ocultar as funções administrativas na interface.
