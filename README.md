# medical-platform

Plataforma de agendamento médico com microsserviços.

## Stack

- Java 21, Spring Boot 4.1.0
- Maven multi-module
- MySQL 8.0, RabbitMQ
- GraphQL, REST, JWT (Spring Security)
- Clean Architecture (domain → application → frameworks)

## Serviços

| Serviço | Porta | Banco | Arquitetura | Descrição |
|---------|-------|-------|-------------|-----------|
| auth-service | 8081 | MySQL | REST | Autenticação Basic → JWT |
| scheduling-service | 8084 | MySQL + Flyway | REST + GraphQL | CRUD consultas, disponibilidade, notificações |
| notification-service | 8082 | - | RabbitMQ consumer | E-mail via SMTP, retry 3x → DLQ |
| history-service | 8083 | (memória) | GraphQL | Histórico de consultas |

### Detalhamento

**auth-service**: Login via HTTP Basic → JWT Bearer. Três perfis: MEDICO, ENFERMEIRO, PACIENTE.

**scheduling-service** (producer RabbitMQ):
- CRUD de consultas + horários disponíveis
- Publica notificação em `medical_exchange` (fanout) ao criar/editar consulta
- Clean Architecture com ports e adapters
- Payload de notificação:
```json
{
  "patientId": 3,
  "patientName": "Joao Lima",
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

**notification-service** (consumer RabbitMQ):
- Consome `email_queue` com ACK manual
- Retry 3x com backoff de 1s → DLQ (`dlq_medical_exchange` → `dlq_queue`)
- Envia HTML com template: "Olá, {patientName}!"
- Requer SMTP configurado (Mailtrap/Gmail)

**history-service**: GraphQL com filtro de consultas futuras. Adaptador em memória (integração futura com scheduling-service).

## Docker Compose (unificado)

Subir todos os serviços com dependências:

```bash
# 1. Configurar SMTP (obrigatório para e-mail)
cp .env.example .env
# Editar .env com credenciais SMTP (Mailtrap/Gmail)

# 2. Subir tudo
docker compose up -d

# Ou subir serviços específicos
docker compose up -d notification-service rabbitmq
```

Parar e limpar volumes:

```bash
docker compose down -v
```

### Portas mapeadas

| Serviço | Host | Container |
|---------|------|-----------|
| mysql-auth | 3303 | 3306 |
| mysql-scheduling | 3306 | 3306 |
| rabbitmq | 5672, 15672 | 5672, 15672 |
| auth-service | 8081 | 8080 |
| notification-service | 8082 | 8080 |
| history-service | 8083 | 8083 |
| scheduling-service | 8084 | 8084 |

### Topologia RabbitMQ

```
scheduling-service (producer) → medical_exchange (fanout) → email_queue
                                                                ↓
                            notification-service → ACK (OK) ou NACK (falha 3x)
                                                                ↓
                                                  dlq_medical_exchange → dlq_queue
```

### Variáveis de ambiente

| Variável | Padrão | Obrigatório |
|----------|--------|-------------|
| SMTP_HOST | smtp.gmail.com | Não (se não usar e-mail) |
| SMTP_USERNAME | - | Sim (para enviar e-mail) |
| SMTP_PASSWORD | - | Sim (para enviar e-mail) |
| SMTP_FROM | SMTP_USERNAME | Não |
| RABBITMQ_USER | notification | Não |
| RABBITMQ_PASSWORD | guest | Não |
| JWT_SECRET | chave-local-dev | Não |

## Build local

```bash
# Compilar módulo específico
mvn compile -pl notification-service -am

# Testar módulo específico
mvn test -pl notification-service -am

# Build completo (pular testes de integração)
mvn clean install -DskipTests

# Build completo com ITs (requer Docker)
mvn clean install
```

## Testes manuais

1. Suba tudo: `docker compose up -d --build`
2. Obter token JWT:
   ```bash
   curl -u medico@hospital.com:Senha@123 -X POST http://localhost:8084/api/v1/auth/login
   ```
   Resposta: `{"accessToken": "eyJ...", "tokenType": "Bearer", "expiresIn": 3600000}`

3. Criar consulta (dispara notificação no RabbitMQ):
   ```bash
   TOKEN="eyJ..."  # token do passo anterior
   curl -X POST http://localhost:8084/api/v1/consultas \
     -H "Authorization: Bearer $TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
       "pacienteId": 3,
       "profissionalId": 1,
       "dataConsulta": "2026-10-01T14:30:00",
       "tipo": "PRESENCIAL"
     }'
   ```
   Usuários de demonstração: `medico@hospital.com` / `paciente@hospital.com` (senha: `Senha@123`).

4. Ver logs:
   ```bash
   docker compose logs scheduling-service --tail 5 | grep "Publicando"
   docker compose logs notification-service --tail 10
   ```