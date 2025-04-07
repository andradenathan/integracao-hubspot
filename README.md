# Integração Hubspot - Case Técnico

Integração entre o Hubspot com o sistema desenvolvido para o case técnico da Meetime, permitindo a sincronização de contatos e informações entre as duas plataformas.


## Endpoints
**/auth/authorization-url**: Endpoint para gerar sua URL de autorização. Necessário ter atualizado o `application.properties` adicionando as seguintes variáveis:
- HUBSPOT_CLIENT_ID: ID do cliente obtido no painel de desenvolvedor do HubSpot.
- HUBSPOT_CLIENT_SECRET: Segredo do cliente obtido no painel de desenvolvedor do HubSpot. 

**/hubspot/contact**: Endpoint para criar um contato no HubSpot.

**/webhook**: Endpoint para lidar com os eventos recebidos do Webhook do HubSpot.

**/contacts**: Endpoint para listar os contatos criados na API. 

## Como Executar
O projeto utiliza o **Ngrok** para expor o servidor local da API em um domínio público. Isso é necessário para que o HubSpot consiga enviar webhooks ao seu ambiente de desenvolvimento.

Além disso, disponibilizei um serviço em Python que faz somente o papel de registrar a URL pública que o Ngrok gerar. Esse serviço é necessário para que o HubSpot consiga enviar os eventos de criação de contatos para a API.

### Passo a passo para rodar o projeto

1. Suba o banco de dados com o Docker:
   ```bash
   docker-compose -f infra/docker-compose.yml up -d db
   ```

2. Suba a API com Docker:

   ```bash
   docker compose -f infra/docker-compose.yml up -d app
   ```

3. Em seguida, inicie o container do Ngrok com seu token de autenticação:
```bash
NGROK_AUTHTOKEN={SEU_NGROK_AUTHTOKEN} docker compose -f infra/docker-compose.yml up -d ngrok --no-deps
```
Um token para o Ngrok foi enviado através do e-mail dos avaliadores do desafio. Caso você não tenha um token, confira as instruções clicando aqui: [Como obter seu token de autenticação NGROK](/docs/README.md).

4. Após o container do Ngrok subir, suba o container do serviço de salvar a url na api:
```bash 
docker-compose -f infra/docker-compose.yml up -d public-url-registry --no-deps --build
```

5. Para pegar a URL pública do Ngrok, execute o seguinte comando:
```bash
docker logs public-url-registry
```
onde a resposta será algo como:
```bash
Ngrok public URL: https://b758-187-46-87-135.ngrok-free.app
Response from app: 200 {"data":null,"message":"Public webhook URL set successfully","status":"success"}
```
6. Copie a URL pública do Ngrok e adicione o endpoint "/webhook" e adicione o endereço completo no HubSpot, na seção de webhooks, para que o HubSpot consiga enviar os eventos de criação de contatos para a API.

## Tecnologias
- Java 17
- Maven
- Spring Boot
- PostgreSQL
- Docker
- Ngrok

## Bibliotecas

### Spring Security
Motivação: Escolhi o Spring Security porque precisava garantir que apenas requisições autenticadas com o token de acesso do HubSpot pudessem acessar os endpoints da aplicação.
Optei por essa biblioteca por ser nativa do ecossistema Spring, e muito utilizada com diversas possibilidades de configuração para lidar com autenticação via Bearer Token de forma robusta.

### Spring Web
Motivação:
Para a construção da API RESTful, optei pelo Spring Web porque ele oferece uma forma muito prática de estruturar endpoints usando anotações como @RestController e @RequestMapping. Como já estou familiarizado com o Spring Boot, estou acostumado a lidar com a facilidade dessa bilioteca para gerenciar requisições RESTful.

### Spring Retry
Motivação: Em especial, como fora explicitado no paper do desafio técnico, a necessidade de lidar com retries entre a comunicação da API e o HubSpot, escolhi essa lib pois ela é uma das mais utilizadas para implementar lógica de retry em aplicações Spring. O Spring Retry permite configurar facilmente a quantidade de tentativas, o intervalo entre elas e outras opções de configuração, até mesmo tentativas utilizando concorrência, tornando a implementação de lógica de retry mais simples e eficiente. 


## Design de arquitetura

Este projeto utiliza uma abordagem **modular por feature**, onde cada funcionalidade da aplicação (como contatos, autenticação e webhooks) possui seu próprio pacote contendo controladores, DTOs e serviços relacionados. Isso facilita a leitura e o entendimento do projeto por parte dos avaliadores, ao mesmo tempo em que mantém separação de responsabilidades.

Optei por **não utilizar Clean Architecture** neste desafio técnico devido à sua complexidade desnecessária para um escopo reduzido e com tempo limitado. A arquitetura escolhida favorece a simplicidade (princípio **KISS**) e evita repetição (**DRY**), mantendo o projeto organizado e facilmente navegável.

Apesar da simplicidade, os princípios **SOLID** foram respeitados durante a implementação, garantindo coesão e manutenibilidade.


## Melhorias futuras

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