# History Service

Modulo responsavel pela consulta do historico de consultas dos pacientes por GraphQL.

## Escopo desta primeira entrega

- query de historico completo;
- filtro para apenas consultas futuras;
- ordenacao deterministica por data e identificador;
- porta de saida para desacoplar a origem dos dados;
- testes unitarios, de contrato GraphQL e de contexto;
- execucao na porta `8083`.

Autenticacao, persistencia e mensageria ainda nao foram implementadas porque dependem de contratos do grupo. O adaptador atual trabalha em memoria e inicia vazio; ele existe para validar o fluxo e sera substituido pela fonte real de dados em uma entrega posterior.

## Arquitetura

```text
presentation/graphql -> application/usecase -> domain/gateway
                                      ^
                                      |
                              infra/adapter
```

- `domain`: modelo e porta de acesso aos dados, sem dependencia do Spring;
- `application`: regra para buscar, filtrar e ordenar o historico;
- `infra`: composicao dos beans e adaptador temporario em memoria;
- `presentation`: controller, mapper e DTO de resposta GraphQL.

## Executar

No diretorio `history-service`:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Endpoint GraphQL: `http://localhost:8083/graphql`

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

Enquanto a fonte real de dados nao estiver integrada, a resposta esperada e:

```json
{
  "data": {
    "appointmentHistory": []
  }
}
```

O campo `scheduledAt` usa o formato ISO-8601 com offset, por exemplo `2026-09-01T13:00:00-03:00`. O filtro futuro compara o instante representado nesse valor e nao inclui uma consulta exatamente igual ao horario atual.

## Proximas decisoes da equipe

- como a identidade autenticada e os perfis chegam ao `history-service`;
- qual servico e a fonte do historico e se existira banco proprio;
- se o Historico recebera dados por chamada ou evento;
- quais campos o medico podera editar.
