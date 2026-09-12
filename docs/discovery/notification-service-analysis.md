# Medical Platform — Análise do Projeto

> Documento gerado em: 2026-09-10
> Branch atual: `feature/notification-service`

---

## 1. Visão Geral

**Repositório:** `medical-platform` (multi-módulo Maven)
**Java:** 21 | **Spring Boot:** 4.1.0 (SB 3.x)
**BD:** MySQL | **Mensageria:** RabbitMQ | **GraphQL:** Spring GraphQL
**Portão padrão:** 8080 (root), cada serviço tem sua porta

**Módulos declarados no parent POM:**

```
medical-platform (pom)
├── auth-service
├── scheduling-service
├── notification-service     ← branch atual
└── history-service
```

**Observação:** Não há módulo/common compartilhado. Cada serviço é independente, sem lib interna de domínio ou DTOs compartilhados.

---

## 2. Serviços Existentes

### 2.1 auth-service — `feature/auth-service` ✅ Completo

| Item | Status |
|------|--------|
| Arquitetura | Clean Architecture (domain → application → infra → presentation) |
| Entidades | `User` (id, nome, email, password, telefone, role, isActive, createdAt, updatedAt) |
| Enums | `Role` (ADMIN, DOCTOR, PATIENT) |
| Segurança | JWT (access + refresh tokens), bcrypt, Spring Security filter chain |
| Endpoints REST | `POST /api/v1/auth/**`, `GET/POST/PUT/DELETE /api/v1/users/**` |
| OpenAPI | Swagger UI configurado (`OpenApiConfig`) |
| Exception handling | Global handlers + `ProblemDetailFactory` |
| Validação | Bean Validation (`@NotBlank`, `@Email`, etc.) |
| Testes | Unitários (use cases, adapter, controller, entity) |
| Banco | JPA + H2 (dev) / MySQL (prod) — `application.properties` |
| Porta | 8081 |
| Docs | Anotações OpenAPI nos controllers |

**Use Cases implementados:**
- CreateUser, FindById/Email/UserName, List, UpdateProfile, UpdatePassword, UpdateRole, DeleteById
- AuthenticateUser, RefreshToken

### 2.2 scheduling-service — `schedulling-service` ✅ Completo

| Item | Status |
|------|--------|
| Arquitetura | Clean Architecture adaptada (domain → application → frameworks → interface_adapters) |
| Entidades | `Consulta`, `AvailableSlot`, `Usuario` |
| Enums | `ConsultaStatus` (AGENDADA, CONFIRMADA, CANCELADA, REALIZADA), `ConsultaTipo` (CONSULTA, RETORNO, EXAME), `UserRole` |
| Segurança | JWT próprio (valida token, integra com auth-service) |
| Endpoints REST | CRUD consultas, disponibilidade, auth interno |
| GraphQL | `ConsultaFiltroInput` para consultas com filtros |
| RabbitMQ | **Publisher** — publica eventos no exchange `medical_exchange` |
| DLQ | Dead-letter queue configurada (`dlq_medical_exchange` → `dlq_queue`) |
| Migrations | Flyway habilitado |
| Testes | Unitários + Integração (RabbitMQ mockado, JPA integrado) |
| Banco | MySQL (`scheduling_db`) |
| Porta | 8084 |

**Use Cases:** CriarConsulta, EditarConsulta, CancelarConsulta, ListarConsultas (com filtros), ConsultarDisponibilidade

**RabbitMQ — contrato do publisher:**

```java
// Exchange: medical_exchange (fanout)
// Queue:    email_queue (com DLQ bindada)
// Payload:
record ReservationNotificationPayload(
    Long patientId,
    String patientEmail,
    String patientNumber,
    ConsultationDetails consultation
) {
    record ConsultationDetails(
        Long consultationId,
        String doctorName,
        String medicalSpecialty,
        LocalDateTime consultationDate
    );
}
```

### 2.3 history-service — `feature/history-service` 🔶 Parcial

| Item | Status |
|------|--------|
| Arquitetura | Clean Architecture (esboço) |
| Features | Apenas `GetAppointmentHistoryUseCase` |
| GraphQL | Controller + Mapper + Response implementados |
| Persistência | **InMemoryAppointmentHistoryGateway** — sem banco real |
| Testes | Unitários (use case, gateway, controller) |
| Banco | Nenhum configurado |
| Porta | Não definida |
| Discovery | `docs/discovery/history-service.md` **não existe** (esperado) |

**Pendências:** implementar JPA real, filtros de histórico, GraphQL schema completo, porta, Docker.

---

## 3. notification-service — Estado Atual (branches `main` / `feature/notification-service`)

```
notification-service/
├── pom.xml                          ← apenas JPA + WebMVC (sem RabbitMQ)
├── src/main/java/.../
│   └── NotificationServiceApplication.java   ← vazio
├── src/main/resources/
│   └── application.properties       ← só spring.application.name
└── src/test/java/.../
    └── NotificationServiceApplicationTests.java  ← vazio
```

**Status:** ⬜ Esqueleto — sem lógica de negócio, sem dependências RabbitMQ, sem configuração.

---

## 4. Integração RabbitMQ — Mapa Atual

```
scheduling-service                          notification-service
┌──────────────────────┐     Fanout         ┌──────────────────────┐
│ ConsultaEventPublisher│ ─────────────────→ │      ???             │
│ (ReservationPublisher)│  medical_exchange  │  (consumer ausente)  │
│                      │         ↓          │                      │
│   Payload publicado  │   email_queue      │  Deve consumir de    │
│                      │   (com DLQ)        │  email_queue e       │
└──────────────────────┘                    │  processar notify    │
                                            └──────────────────────┘
```

- Exchange: `medical_exchange` (fanout, durável)
- Queue: `email_queue` (durável, com DLQ)
- DLQ exchange: `dlq_medical_exchange` (direct)
- DLQ queue: `dlq_queue` (routing key: `dlq.notification`)
- Payload: `ReservationNotificationPayload`

---

## 5. Árvore de Arquivos (todas as branches)

```
medical-platform/
├── pom.xml                         ← parent POM (Java 21, 4 módulos)
├── .mvn/settings.xml               ← Maven Central + Spring repos
├── README.md
│
├── auth-service/                   ← feature/auth-service
│   ├── pom.xml
│   ├── docker-compose.yml
│   └── src/
│       ├── main/java/.../authservice/
│       │   ├── AuthServiceApplication.java
│       │   ├── application/
│       │   │   ├── authentication/  (dto, mapper, usecase)
│       │   │   └── user/            (dto, mapper, usecase)
│       │   ├── domain/
│       │   │   ├── authentication/  (gateways, model, exceptions)
│       │   │   └── user/            (entity, enums, exceptions, gateways)
│       │   ├── infra/
│       │   │   ├── config/          (AuthConfig, OpenApiConfig, UserUseCaseConfig)
│       │   │   ├── security/        (JWT, filters, PasswordConfig, SecurityConfig)
│       │   │   └── user/            (adapter, mapper, persistence)
│       │   └── presentation/
│       │       ├── authentication/  (controller, docs, requests, responses)
│       │       └── user/            (controller, docs, exception, requests, responses)
│       └── test/                    (use cases, adapter, controller, entity)
│
├── scheduling-service/             ← schedulling-service
│   ├── pom.xml
│   ├── docker-compose.yml
│   ├── README.md
│   └── src/
│       ├── main/java/.../schedulingservice/
│       │   ├── SchedulingServiceApplication.java
│       │   ├── application/        (dtos, services)
│       │   ├── config/             (OpenApiConfig)
│       │   ├── domain/
│       │   │   ├── auth/           (TokenEmitido, TokenService, UsuarioAutenticado)
│       │   │   ├── entities/       (Consulta, AvailableSlot, Usuario + enums)
│       │   │   ├── events/         (ConsultaEventPublisher interface)
│       │   │   ├── exceptions/     (6 classes)
│       │   │   ├── repositories/   (interfaces + filtro)
│       │   │   └── usecases/       (comandos + interfaces)
│       │   ├── frameworks/
│       │   │   ├── persistence/    (adapters, entities JPA, mappers, repositories, specs)
│       │   │   ├── rabbitmq/       (RabbitMQConfig, ReservationPublisherService, payload)
│       │   │   └── security/       (JWT filter, service, UserDetails)
│       │   └── interface_adapters/ (controllers REST + GraphQL, exception handling, mappers)
│       └── test/                   (services, entities, repository IT, RabbitMQ)
│
├── notification-service/           ← feature/notification-service (ATUAL)
│   ├── pom.xml                     ← sem RabbitMQ, sem mail
│   └── src/
│       ├── main/java/.../notificationservice/
│       │   └── NotificationServiceApplication.java
│       ├── main/resources/
│       │   └── application.properties
│       └── test/
│           └── NotificationServiceApplicationTests.java
│
└── history-service/                ← feature/history-service
    ├── pom.xml
    ├── README.md
    └── src/
        ├── main/java/.../historyservice/
        │   ├── HistoryServiceApplication.java
        │   ├── application/history/usecase/GetAppointmentHistoryUseCase.java
        │   ├── domain/history/
        │   │   ├── gateway/AppointmentHistoryGateway.java
        │   │   └── model/AppointmentHistory.java
        │   ├── infra/
        │   │   ├── config/HistoryUseCaseConfig.java
        │   │   └── history/adapter/InMemoryAppointmentHistoryGateway.java
        │   └── presentation/history/graphql/
        │       ├── controller/HistoryGraphQlController.java
        │       ├── mapper/HistoryGraphQlMapper.java
        │       └── response/AppointmentHistoryResponse.java
        └── test/                    (use case, adapter, controller)
```

---

## 6. Próximos Passos — Notification Service

Baseado na análise, o `notification-service` precisa:

### 6.1 Dependências Maven
- `spring-boot-starter-amqp` (RabbitMQ consumer)
- `spring-boot-starter-mail` (envio de email)
- `jackson-databind` (já incluso via spring-boot-starter-web, mas confirmar)
- `lombok` (padrão do projeto)
- H2 para testes

### 6.2 Consumer RabbitMQ
- Consumir `email_queue` do exchange `medical_exchange`
- Processar `ReservationNotificationPayload`
- Tratar falhas → rejeitar mensagem → DLQ
- Implementar retry + DLQ como no scheduling-service

### 6.3 Lógica de Notificação
- Email para paciente (dados da consulta: médico, especialidade, data)
- Possível extensão: SMS, push notification
- Template de email (Thymeleaf ou string simples)

### 6.4 Configuração
- `application.yml` com rabbitmq host/port/credentials
- Queue bindings para `email_queue`
- Propriedades de email (SMTP)

### 6.5 Testes
- Unitários para o consumer
- Integration test com RabbitMQ mockado (TestContainers ou `@SpringRabbitTest`)
- Teste de DLQ (mensagem inválida → dead letter)

---

## 7. Padrões de Projeto Identificados

| Padrão | Onde |
|--------|------|
| **Clean Architecture** | auth-service, scheduling-service, history-service |
| **Repository Pattern** | Todos os serviços (JpaRepository + interface de domínio) |
| **Use Case / Interactor** | auth-service, scheduling-service |
| **Publisher-Subscriber (Event-Driven)** | scheduling-service → RabbitMQ → notification-service |
| **JWT Authentication** | auth-service (emissor), scheduling-service (validador) |
| **GraphQL** | scheduling-service, history-service |
| **OpenAPI / Swagger** | auth-service, scheduling-service |
| **Global Exception Handler** | auth-service, scheduling-service |
| **DTO/Mapper Pattern** | Todos os serviços (separação camadas) |

---

## 8. Riscos e Observações

1. **Payload compartilhado:** `ReservationNotificationPayload` está no scheduling-service. Notification-service precisa do mesmo record — duplicar ou extrair para lib compartilhada.
2. **Spring Boot 4.1.0:** É SB 3.x com nomenclatura nova. Verificar compatibilidade de versões RabbitMQ/Spring AMQP.
3. **DLQ já configurada:** scheduling-service já aponta DLQ. Consumer precisa tratar rejeição correta p/ não perder mensagens.
4. **Sem módulo common:** DTOs de usuário duplicados entre auth-service e scheduling-service. Considerar criar `medical-common` se crescer.
5. **history-service inconsistente:** Usa in-memory gateway, sem JPA. Pode impactar se notification-service precisar de dados de histórico.
6. **Porta do notification-service:** Não definida atualmente (default 8080). Definir, ex: 8082.