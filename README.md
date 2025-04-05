# Integração Hubspot - Case Técnico

## Descrição
Integração entre o Hubspot com o sistema desenvolvido para o case técnico da Meetime, permitindo a sincronização de contatos e informações entre as duas plataformas.


## Como Executar
O projeto utiliza o **Ngrok** para expor o servidor local da API em um domínio público. Isso é necessário para que o HubSpot consiga enviar webhooks ao seu ambiente de desenvolvimento.

### Passo a passo para rodar o projeto

1. Suba a API com Docker:

   ```bash
   docker compose -f infra/docker-compose.yml up -d app
   ```

2. Em seguida, inicie o container do Ngrok com seu token de autenticação:
    ```bash
    NGROK_AUTHTOKEN={SEU_NGROK_AUTHTOKEN} docker compose -f infra/docker-compose.yml up -d ngrok --no-deps
   ```
Um token para o Ngrok foi enviado através do e-mail dos avaliadores do desafio. Caso você não tenha um token, confira as instruções clicando aqui: [Como obter seu token de autenticação NGROK](/docs/README.md).

3.  Após o container subir, obtenha o endereço público do túnel gerado:
  ```bash
    docker logs infra-ngrok-1
   ```

4. O log exibirá algo como:
  ```bash
  t=2025-04-05T22:03:29+0000 lvl=info msg="started tunnel" obj=tunnels name=command_line addr=http://app:8090 url=https://ae06-187-46-87-135.ngrok-free.app
  ```
onde neste caso, `https://ae06-187-46-87-135.ngrok-free.app` é o domínio público gerado pelo Ngrok.

5. Copie esse endereço e registre no painel de Webhooks do seu aplicativo no HubSpot, usando o endpoint completo:
    ```bash
    https://ae06-187-46-87-135.ngrok-free.app/webhooks
    ```

## Tecnologias
- Java 17
- Maven
- Spring Boot
- PostgreSQL
- Docker
- Ngrok

## Bibliotecas

### Spring Security
Utilizado para proteger as rotas da API com autenticação via Bearer Token (o token de acesso do hubspot), garantindo que apenas requisições autenticadas possam acessar os endpoints sensíveis do sistema.

### Spring Web
Responsável por fornecer os recursos de construção da API RESTful, incluindo os decorators @RestController e @RequestMapping, além de facilitar o gerenciamento de requisições HTTP.

### Spring Retry
Em especial, como fora explicitado no paper do desafio técnico, a necessidade de lidar com retries entre a comunicação da API e o HubSpot, escolhi essa lib pois ela é uma das mais utilizadas para implementar lógica de retry em aplicações Spring. O Spring Retry permite configurar facilmente a quantidade de tentativas, o intervalo entre elas e outras opções de configuração, tornando a implementação de lógica de retry mais simples e eficiente. 


### Design de arquitetura

Este projeto utiliza uma abordagem **modular por feature**, onde cada funcionalidade da aplicação (como contatos, autenticação e webhooks) possui seu próprio pacote contendo controladores, DTOs e serviços relacionados. Isso facilita a leitura e o entendimento do projeto por parte dos avaliadores, ao mesmo tempo em que mantém separação de responsabilidades.

Optei por **não utilizar Clean Architecture** neste desafio técnico devido à sua complexidade desnecessária para um escopo reduzido e com tempo limitado. A arquitetura escolhida favorece a simplicidade (princípio **KISS**) e evita repetição (**DRY**), mantendo o projeto organizado e facilmente navegável.

Apesar da simplicidade, os princípios **SOLID** foram respeitados durante a implementação, garantindo coesão e manutenibilidade.


### Melhorias futuras

#### Arquitetura
- **Adoção da Clean Architecture**: Para tornar o projeto mais escalável e independente de frameworks externos, é possível reorganizar as camadas em `domain`, `application`, `infrastructure` e `interfaces`, seguindo os princípios da Clean Architecture.
- **Uso de interfaces para inversão de dependência (DIP)**: Separar a lógica de negócio da tecnologia adotada (como o Spring WebClient) utilizando interfaces e injeção de dependência.

#### Segurança
- **Validação mais robusta do token OAuth**: Implementar validações de expiração e escopo do token para maior segurança.
- **Rotacionamento e renovação automática de tokens**: Automatizar a renovação do `access_token` para evitar falhas por expiração.

#### Observabilidade
- **Integração com ferramentas de logging**: Utilizar de tecnologias como Grafana para monitoramento em tempo real.

#### Qualidade de Código e Testes
- **Cobertura de testes unitários**: Aumentar cobertura dos serviços, DTOs e controladores com JUnit e Mockito.
- **Testes de integração**: Validar a integração real com a API do HubSpot em ambiente isolado (ex: Testcontainers).

#### Outras melhorias
- **Paginação e filtros para consultas futuras**: Caso o projeto evolua para consumir múltiplos contatos da API HubSpot.
- **Documentação da API com Swagger**: Para facilitar testes e entendimento das rotas expostas.