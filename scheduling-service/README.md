# Scheduling Service — Agendamento de Consultas

Serviço de agendamento de consultas de uma plataforma hospitalar, construído com Spring Boot e
Clean Architecture. Suporta CRUD de consultas, consulta de disponibilidade, autenticação
Basic → JWT com três perfis de acesso, publicação de notificações no RabbitMQ e consulta de
histórico via GraphQL.

Este serviço é o **producer** das notificações de agendamento: ele publica no RabbitMQ e os
demais microsserviços (ex.: MS de notificação) consomem essas mensagens — o consumo em si não faz
parte deste repositório.

## Stack

- Java 21, Spring Boot 4.1.0 (Spring Security 7.x, Spring Data JPA, Spring for GraphQL, Spring AMQP)
- MySQL 8.0 + Flyway (migrations em `src/main/resources/db/migration`)
- RabbitMQ (exchanges/filas declaradas por este serviço)
- Lombok, MapStruct
- JUnit 5, Mockito, AssertJ, Testcontainers

> Nota de versão: o `pom.xml` do repositório já estava configurado com
> `spring-boot-starter-parent 4.1.0` (Spring Boot 4) antes deste trabalho começar. Os requisitos
> originais mencionavam Spring Boot 3.x / Spring Security 6.x; por decisão do time, o projeto
> permaneceu na versão 4.1.0 já commitada, adaptando as APIs onde necessário (ex.:
> `spring-boot-starter-webmvc` no lugar de `spring-boot-starter-web`, pacotes de test-autoconfigure
> modularizados).

## Arquitetura (Clean Architecture)

```
br.com.medical.schedulingservice/
├── domain/                 # Entidades, portas (repositories/events/auth) e casos de uso — sem dependência de framework
│   ├── entities/
│   ├── repositories/       # Portas de saída (interfaces) + objetos de filtro
│   ├── usecases/           # Interfaces de caso de uso + comandos de entrada
│   ├── events/              # Porta de publicação de eventos
│   ├── auth/                 # Portas de token/usuário autenticado
│   └── exceptions/
├── application/
│   ├── dtos/                # Contratos de entrada/saída da API (REST/GraphQL)
│   └── services/            # Implementações dos casos de uso (orquestram os ports)
├── interface_adapters/
│   ├── controllers/          # REST controllers
│   ├── graphql/              # Controller GraphQL + input types + tratamento de erros
│   ├── mappers/               # MapStruct: domínio -> DTO de resposta
│   └── exceptionhandling/    # @RestControllerAdvice — payload de erro padronizado
├── frameworks/
│   ├── persistence/          # Entidades JPA, repositórios Spring Data, adapters dos ports de domínio
│   ├── rabbitmq/               # Topologia (exchanges/filas/DLQ), publisher e payload de notificação
│   └── security/              # JWT, Basic Auth, filtros e configuração do Spring Security
└── config/                   # Configuração transversal (OpenAPI)
```

A regra de dependência é respeitada de fora para dentro: `frameworks` e `interface_adapters`
dependem de interfaces definidas em `domain`; nunca o inverso. As implementações concretas
(JPA, RabbitMQ, JWT) são injetadas via Spring nas portas definidas no domínio — trocar Kafka por
RabbitMQ não exigiu nenhuma mudança em `domain` ou `application`, só a troca do adapter em
`frameworks` (`ReservationPublisherService` implementa a mesma porta `ConsultaEventPublisher`).

**Trade-off assumido conscientemente**: os controllers usam os tipos `UserDetails`
(`AuthController`) e `UsuarioAutenticado` (demais controllers) para representar o usuário
autenticado — o segundo vive em `domain.auth` justamente para não acoplar os controllers a classes
de `frameworks.security`.

## Perfis e permissões

| Perfil | Criar/Editar/Cancelar consulta | Listar todas as consultas | Ver histórico próprio |
|---|---|---|---|
| `MEDICO` | ✅ | ✅ | ✅ |
| `ENFERMEIRO` | ✅ | ✅ | ✅ |
| `PACIENTE` | ❌ | ❌ (só as próprias) | ✅ |

A autorização é validada em duas camadas: `@PreAuthorize` nos controllers (REST e GraphQL) e
regras de negócio no `application/services` (ex.: um paciente nunca recebe consultas de outro
paciente, mesmo que tente manipular o filtro da query).

## Autenticação

1. `POST /api/v1/auth/login` — autenticado via **HTTP Basic** (email/senha). O Spring Security
   valida as credenciais (`BCryptPasswordEncoder`) antes do controller ser executado.
2. A resposta traz um **JWT** (`Bearer`) que deve ser enviado no header `Authorization` de todas as
   demais chamadas (REST e GraphQL).

```bash
curl -u medico@hospital.com:Senha@123 -X POST http://localhost:8084/api/v1/auth/login
```

## Usuários de demonstração (seed Flyway `V4__seed_demo_users.sql` / `V5__add_telefone_especialidade_to_users.sql`)

| Email | Senha | Perfil | Telefone | Especialidade |
|---|---|---|---|---|
| medico@hospital.com | Senha@123 | MEDICO | 4002-8922 | Cardiologia |
| enfermeiro@hospital.com | Senha@123 | ENFERMEIRO | 4002-8922 | — |
| paciente@hospital.com | Senha@123 | PACIENTE | 4002-8922 | — |

## Subindo o ambiente

```bash
cp .env.example .env
docker-compose up -d --build
```

Isso sobe MySQL, RabbitMQ e a aplicação (porta `8084`). As migrations Flyway (incluindo o
seed de usuários) rodam automaticamente na inicialização.

- Swagger UI: http://localhost:8084/swagger-ui.html
- GraphiQL: http://localhost:8084/graphiql
- Health: http://localhost:8084/actuator/health
- RabbitMQ Management UI: http://localhost:15672 (usuário/senha: `guest`/`guest` por padrão)

Para rodar localmente sem Docker (com MySQL/RabbitMQ já disponíveis), exporte as variáveis do
`.env.example` e rode:

```bash
./mvnw spring-boot:run
```

## Endpoints REST (`/api/v1`)

| Método | Rota | Perfis | Descrição |
|---|---|---|---|
| POST | `/auth/login` | (Basic Auth) | Autentica e emite o JWT |
| POST | `/consultas` | MEDICO, ENFERMEIRO | Cria consulta (publica notificação no RabbitMQ) |
| PUT | `/consultas/{id}` | MEDICO, ENFERMEIRO | Edita consulta (publica notificação no RabbitMQ) |
| GET | `/consultas` | MEDICO, ENFERMEIRO, PACIENTE | Lista com filtros `pacienteId`, `profissionalId`, `status`, `data` |
| GET | `/consultas/{id}` | MEDICO, ENFERMEIRO, PACIENTE (própria) | Busca por id |
| DELETE | `/consultas/{id}` | MEDICO, ENFERMEIRO | Cancela a consulta |
| GET | `/disponibilidade?profissionalId=&data=` | MEDICO, ENFERMEIRO, PACIENTE | Horários livres de um profissional |

Payload de erro padronizado (`ApiError`) em todas as respostas de erro:

```json
{
  "timestamp": "2026-09-10T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Consulta nao encontrada para o id: 99",
  "path": "/api/v1/consultas/99",
  "details": null
}
```

## GraphQL (`/graphql`)

Schema em `src/main/resources/schema.graphqls`.

```graphql
query {
  historicoConsultas(filtro: { status: AGENDADA }) {
    id
    pacienteId
    dataConsulta
    status
  }
}
```

Segue a mesma regra de autorização por perfil dos endpoints REST (pacientes só recebem as
próprias consultas), aplicada via `@PreAuthorize` no `ConsultaGraphQlController` e reforçada no
`ListarConsultasService`.

## Notificações RabbitMQ

Este serviço é o **producer**: declara toda a topologia abaixo e publica em `medical_exchange`
sempre que uma consulta é criada ou editada. O consumo (envio de e-mail) é responsabilidade de
outro microsserviço (ex.: MS de notificação) — não está neste repositório.

```
ReservationPublisherService --> medical_exchange (fanout) --> email_queue
                                                                  |
                                                    consumidor faz ACK (sucesso) ou
                                                    NACK / requeue=false (rejeição)
                                                                  v
                                               dlq_medical_exchange (direct, "dlq.notification")
                                                                  |
                                                                  v
                                                              dlq_queue
```

- **Exchange principal**: `medical_exchange` (fanout)
- **Fila**: `email_queue`, com dead-lettering configurado (`x-dead-letter-exchange` /
  `x-dead-letter-routing-key`) apontando para a DLQ
- **Exchange de DLQ**: `dlq_medical_exchange` (direct), routing key `dlq.notification`
- **Fila de DLQ**: `dlq_queue`
- **Conversor**: JSON (`Jackson2JsonMessageConverter`, reaproveitando o `ObjectMapper` do Spring
  para serializar `LocalDateTime` corretamente)
- **Quando publica**: na criação e na edição de uma consulta (cancelamento não publica — mesma
  decisão já tomada quando o transporte era Kafka)
- **Payload** (`ReservationNotificationPayload`, em `frameworks/rabbitmq/events`):

```json
{
  "patientId": 3,
  "patientEmail": "paciente@hospital.com",
  "patientNumber": "4002-8922",
  "consultation": {
    "consultationId": 10,
    "doctorName": "Dr. Ricardo Silva",
    "medicalSpecialty": "Cardiologia",
    "consultationDate": "2026-12-01T10:00:00"
  }
}
```

`patientNumber` é o telefone do paciente (`users.telefone`) e `medicalSpecialty` é a especialidade
do profissional (`users.especialidade`, preenchida apenas para `MEDICO`) — ambos os campos foram
adicionados via `V5__add_telefone_especialidade_to_users.sql` especificamente para suportar este
payload.

> Este serviço não implementa nenhum `@RabbitListener`: a fila `email_queue` e a `dlq_queue` são
> declaradas aqui (para que a topologia exista e as mensagens fiquem visíveis/inspecionáveis via
> RabbitMQ Management UI), mas o consumo com ACK manual fica a cargo do MS consumidor.

## Testes

```bash
./mvnw test      # testes unitários (services de caso de uso + entidades de domínio) — não requer Docker
./mvnw verify     # inclui os testes de integração (*IT) com Testcontainers — requer Docker
```

- Unitários: um `*ServiceTest` por caso de uso (`Mockito`), cobrindo os caminhos felizes e as
  regras de negócio/erros (acesso negado, conflito de horário, estado inválido etc.).
- Integração (`*IT`, executados via `maven-failsafe-plugin` na fase `verify`):
  - `ConsultaRepositoryImplIT` — adapters de persistência contra MySQL real (valida também as
    migrations Flyway).
  - `SchedulingServiceApplicationIT` — sobe o contexto completo (Web + JPA + Security + RabbitMQ)
    contra containers reais.
- `ReservationPublisherServiceTest` — unitário verificando o payload publicado (dados do paciente
  e do profissional combinados corretamente) tanto na criação quanto na edição de consulta.

## Postman

Importe `postman/scheduling-service.postman_collection.json`. A pasta **Auth** salva os tokens de
cada perfil em variáveis da collection (`tokenMedico`, `tokenEnfermeiro`, `tokenPaciente`),
reutilizadas automaticamente pelas demais requisições.

## Variáveis de ambiente

Veja `.env.example`. As principais:

| Variável | Descrição | Default |
|---|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Conexão MySQL | `jdbc:mysql://localhost:3306/scheduling_db` / `root` / `root` |
| `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD` | Conexão RabbitMQ | `localhost` / `5672` / `guest` / `guest` |
| `RABBITMQ_EXCHANGE_MEDICAL` / `RABBITMQ_EXCHANGE_DLQ` | Nomes dos exchanges | `medical_exchange` / `dlq_medical_exchange` |
| `RABBITMQ_QUEUE_EMAIL` / `RABBITMQ_QUEUE_DLQ` | Nomes das filas | `email_queue` / `dlq_queue` |
| `RABBITMQ_ROUTING_KEY_DLQ` | Routing key da DLQ | `dlq.notification` |
| `JWT_SECRET` | Chave HMAC do JWT (troque em produção) | — |
| `JWT_ACCESS_EXPIRATION` | Expiração do access token (ms) | `3600000` |