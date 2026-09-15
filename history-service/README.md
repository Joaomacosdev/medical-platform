# History Service

Modulo responsavel pela consulta do historico de consultas dos pacientes por GraphQL.

## Escopo implementado

- query de historico completo;
- filtro para apenas consultas futuras;
- ordenacao deterministica por data e identificador;
- persistencia local do read model em banco relacional;
- consumo de eventos RabbitMQ publicados pelo servico de agendamento;
- testes unitarios, de contrato GraphQL e de contexto;
- collection do Postman para smoke test das consultas completa, futura e de validacao;
- imagem Docker multi-stage executada com usuario sem privilegios;
- execucao na porta `8083`.

O servico agora mantem um read model proprio em `appointment_events`, alimentado por eventos RabbitMQ do Servico de Agendamento.

Autenticacao e autorizacao permanecem como integracao pendente. A branch de Autenticacao ja emite JWT com `sub` (`userId`) e `role`, porem o grupo ainda precisa confirmar como `patientId` se relaciona com esse usuario e onde o token sera validado. Ate esse contrato ser fechado, o endpoint nao deve ser apresentado como seguro para dados reais.

O Servico de Agendamento e o proprietario da criacao e edicao das consultas segundo o enunciado oficial. O documento de Engenharia tambem cita a edicao de historico medico como requisito separado, mas nao define campos nem ownership. Por isso, uma mutation de historico nao deve ser inventada antes dessa decisao do grupo.

## Arquitetura

```text
presentation/graphql -> application/usecase -> domain/gateway
                                      ^
                                      |
                              infra/adapter
```

- `domain`: modelo e porta de acesso aos dados, sem dependencia do Spring;
- `application`: regra para buscar, filtrar e ordenar o historico;
- `infra`: composicao dos beans, persistencia JPA/Flyway e consumidor RabbitMQ;
- `presentation`: controller, mapper e DTO de resposta GraphQL.

## Executar

No diretorio `history-service`:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Endpoint GraphQL: `http://localhost:8083/graphql`

Para ambiente integrado com Docker Compose, o servico depende de MySQL e RabbitMQ. O compose principal sobe `mysql-history`, `rabbitmq` e injeta as variaveis de conexao automaticamente no container.

Exemplo com `curl` para consultar o historico completo:

```bash
curl -X POST http://localhost:8083/graphql \
  -H 'Content-Type: application/json' \
  -d '{
    "query": "query AppointmentHistory($patientId: ID!, $futureOnly: Boolean!) { appointmentHistory(patientId: $patientId, futureOnly: $futureOnly) { appointmentId patientId doctorId scheduledAt status } }",
    "variables": {
      "patientId": "patient-1",
      "futureOnly": false
    }
  }'
```

Exemplo com `curl` para consultar somente consultas futuras:

```bash
curl -X POST http://localhost:8083/graphql \
  -H 'Content-Type: application/json' \
  -d '{
    "query": "query AppointmentHistory($patientId: ID!, $futureOnly: Boolean!) { appointmentHistory(patientId: $patientId, futureOnly: $futureOnly) { appointmentId patientId doctorId scheduledAt status } }",
    "variables": {
      "patientId": "patient-1",
      "futureOnly": true
    }
  }'
```

## Executar com Docker

No diretorio `history-service`:

```powershell
docker build -t medical-platform/history-service .
docker run --rm -p 8083:8083 medical-platform/history-service
```

O container expoe a porta `8083` e executa a aplicacao com um usuario sem privilegios.

## Testar no Postman

Importe `history-service.postman_collection.json` e execute uma das requisicoes de smoke test:

- `Consultar historico completo`;
- `Consultar somente consultas futuras`;
- `Validar patientId vazio`.

A collection usa `http://localhost:8083` por padrao. Como a fonte em memoria inicia vazia, ela valida o contrato HTTP/GraphQL, mas nao substitui os testes automatizados do filtro e da ordenacao. Quando a integracao JWT estiver ativa, preencha a variavel `accessToken` com o access token retornado pelo Auth Service.

## Exemplo de query

```graphql
query {
  appointmentHistory(patientId: "patient-1", futureOnly: true) {
    appointmentId
    patientId
    doctorId
    scheduledAt
    status
  }
}
```

Se nenhum evento tiver sido consumido ainda, a resposta esperada e:

```json
{
  "data": {
    "appointmentHistory": []
  }
}
```

O campo `scheduledAt` usa o formato ISO-8601 com offset, por exemplo `2026-09-01T13:00:00-03:00`. O filtro futuro compara o instante representado nesse valor e nao inclui uma consulta exatamente igual ao horario atual.

## Pendencias de integracao do grupo

- definir a relacao entre o `userId` do JWT e o `patientId` usado nas consultas;
- definir onde e como o JWT sera validado pelos demais servicos;
- alinhar o contrato final do evento com os demais integrantes do grupo.
- definir o significado, os campos e o servico proprietario da edicao de historico medico.

O historico agora e alimentado por RabbitMQ. O proximo passo de integracao e validar o fluxo ponta a ponta publicando eventos reais do `scheduling-service` e consultando o GraphQL do `history-service` no ambiente Docker Compose.
