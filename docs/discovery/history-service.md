# Discovery - History Service

## 1. Objetivo

Mapear o escopo do `history-service` antes da implementacao, reconciliando o enunciado oficial da FIAP, o documento complementar de Engenharia de Software, o Trello e a arquitetura atual do repositorio.

Este documento separa:

- **Confirmado**: exigido pela FIAP ou atribuido no Trello;
- **Proposta**: desenho tecnico recomendado para discussao;
- **Pendente**: decisao que depende de outro integrante ou do grupo.

## 2. Fontes e precedencia

1. `ADJT - BB - Tech Challenge - Fase 3.pdf` - fonte normativa.
2. `Documento_Engenharia_Software_TechChallenge_Fase3 (1).pdf` - especificacao complementar.
3. Trello `Entrega TC - 3` - distribuicao operacional do grupo.
4. Codigo da branch `main` - estado real implementado.

Em caso de conflito, prevalece o PDF oficial da FIAP.

## 3. Responsabilidade confirmada

O cartao `[Historico]` esta no Backlog e possui a etiqueta `Vinicius`.

O cartao nao possui descricao, checklist, prazo ou membro formalmente associado. Portanto, a responsabilidade confirmada e o tema **Historico**. O Trello nao atribui a Vinicius toda a autenticacao, toda a mensageria ou o servico de notificacoes.

Dentro do `history-service`, Vinicius e responsavel por:

- contrato GraphQL do historico;
- consultas de historico completo e apenas futuro;
- regras de acesso aplicadas ao historico;
- modelo de leitura e persistencia do historico, caso o servico separado seja mantido;
- consumo dos eventos acordados com Agendamento/Mensageria;
- testes e documentacao do proprio modulo.

## 4. Requisitos funcionais confirmados

### HIS-RF-01 - Consultar historico completo

O sistema deve permitir consultar todos os atendimentos/consultas de um paciente por GraphQL.

### HIS-RF-02 - Consultar somente consultas futuras

O sistema deve permitir filtrar, por GraphQL, somente consultas futuras.

### HIS-RF-03 - Acesso do medico

O medico pode visualizar o historico de pacientes e deve existir uma operacao que permita editar o historico.

O enunciado nao define quais campos do historico podem ser editados. Essa decisao permanece pendente.

### HIS-RF-04 - Acesso do enfermeiro

O enfermeiro pode consultar o historico. O enunciado nao concede ao enfermeiro permissao para editar o historico.

### HIS-RF-05 - Isolamento do paciente

O paciente pode visualizar somente as proprias consultas. O `patientId` recebido do cliente nunca pode permitir que um paciente acesse dados de outro.

### HIS-RF-06 - Refletir criacoes e edicoes

O historico deve refletir consultas criadas e editadas no Servico de Agendamento.

Se a integracao escolhida for assincrona, devem ser processados pelo menos os eventos:

- `APPOINTMENT_CREATED`;
- `APPOINTMENT_UPDATED`.

## 5. Requisitos nao funcionais confirmados

- Spring Boot e Java 21, conforme a stack adotada pelo grupo.
- GraphQL como contrato de consulta.
- controle de acesso por `MEDICO`, `ENFERMEIRO` e `PACIENTE`;
- codigo modular, documentado e testavel;
- instrucoes de configuracao e execucao;
- exemplos importaveis no Postman ou ferramenta equivalente;
- repositorio acessivel aos professores.

## 6. Limite de dominio proposto

Para evitar criar um prontuario medico completo sem requisito, o termo **historico** deve significar inicialmente o historico de consultas/agendamentos do paciente.

Campos minimos propostos para o read model:

| Campo | Finalidade | Estado |
| --- | --- | --- |
| `id` | Identificador interno do registro | Proposta |
| `appointmentId` | Identidade da consulta no Agendamento | Proposta |
| `patientId` | Aplicar filtro e isolamento do paciente | Proposta |
| `doctorId` | Identificar o medico relacionado | Proposta |
| `scheduledAt` | Ordenar e distinguir consultas futuras | Proposta |
| `status` | Representar o estado atual da consulta | Proposta |
| `sourceEventId` | Garantir idempotencia no consumo | Proposta |
| `sourceEventOccurredAt` | Resolver atualizacoes fora de ordem | Proposta |
| `createdAt` | Auditoria local | Proposta |
| `updatedAt` | Auditoria local | Proposta |

Dados clinicos, diagnosticos, prescricoes e anotacoes medicas nao devem ser inventados antes da equipe definir o significado de "editar o historico".

## 7. Contrato GraphQL inicial

```graphql
type Query {
  appointmentHistory(
    patientId: ID
    futureOnly: Boolean! = false
  ): [AppointmentHistoryItem!]!
}

type AppointmentHistoryItem {
  appointmentId: ID!
  patientId: ID!
  doctorId: ID
  scheduledAt: String!
  status: String!
  createdAt: String!
  updatedAt: String!
}
```

Regras propostas:

- `MEDICO` e `ENFERMEIRO`: informam `patientId` obrigatoriamente;
- `PACIENTE`: o servico deriva a identidade do usuario autenticado e rejeita tentativa de consultar outro paciente;
- resultado ordenado por `scheduledAt` de forma deterministica;
- `futureOnly = true`: considerar `scheduledAt > now` no timezone acordado;
- ausencia de registros retorna lista vazia, nunca `null`.

A operacao de edicao pelo medico sera definida separadamente como mutation GraphQL ou endpoint REST depois que os campos editaveis forem aprovados.

## 8. Contrato de evento proposto

O contrato abaixo deve ser aprovado com Agendamento e Mensageria antes da implementacao do consumidor:

```json
{
  "eventId": "uuid",
  "eventType": "APPOINTMENT_CREATED | APPOINTMENT_UPDATED",
  "schemaVersion": 1,
  "occurredAt": "2026-08-24T15:00:00Z",
  "appointmentId": "uuid",
  "patientId": "uuid",
  "doctorId": "uuid",
  "scheduledAt": "2026-09-01T13:00:00-03:00",
  "status": "SCHEDULED"
}
```

Regras propostas do consumidor:

- usar `eventId` para impedir processamento duplicado;
- aplicar `CREATED` como criacao do read model;
- aplicar `UPDATED` como atualizacao do registro existente;
- nao substituir estado mais novo por evento antigo;
- registrar falha de payload sem perder rastreabilidade;
- nao compartilhar entidades JPA entre os modulos.

## 9. Contrato de autorizacao necessario

O `history-service` precisa receber uma identidade autenticada contendo, no minimo:

- `userId`;
- `role`;
- `patientId`, quando o usuario representar um paciente.

Essa informacao ainda nao esta disponivel na `main`. O grupo deve escolher como ela chega ao servico:

1. HTTP Basic validado localmente em cada API;
2. token emitido por Autenticacao e validado pelos demais servicos;
3. gateway que autentica e propaga identidade de forma confiavel.

Uma chamada remota ao servico de Autenticacao em toda requisicao nao e a opcao recomendada, pois cria acoplamento sincrono e um ponto unico de falha.

## 10. Criterios de aceite

### Consultas GraphQL

- [ ] Medico consulta todo o historico de um paciente.
- [ ] Enfermeiro consulta todo o historico de um paciente.
- [ ] Filtro `futureOnly` retorna somente `scheduledAt > now`.
- [ ] Paciente consulta o proprio historico.
- [ ] Paciente nao acessa historico de outro paciente.
- [ ] Usuario sem autenticacao recebe resposta de nao autenticado.
- [ ] Perfil sem permissao recebe resposta de acesso negado.
- [ ] Lista vazia e retornada como `[]`.
- [ ] Resultado possui ordenacao deterministica.

### Integracao assincrona

- [ ] Evento de criacao gera registro no read model.
- [ ] Evento de edicao atualiza o registro correto.
- [ ] Reentrega do mesmo `eventId` nao duplica efeito.
- [ ] Evento invalido segue a politica de erro definida pelo grupo.

### Edicao do historico

- [ ] Medico consegue editar apenas os campos aprovados.
- [ ] Enfermeiro e paciente nao conseguem editar.
- [ ] Alteracao preserva auditoria minima.

## 11. Backlog pronto para o Trello

### HIS-01 - Fechar o dominio do historico

- confirmar se historico significa consultas/agendamentos;
- definir campos retornados;
- definir campos editaveis pelo medico;
- definir status validos;
- definir timezone e ordenacao.

**Aceite:** modelo e regras aprovados pela equipe.

### HIS-02 - Fechar o contrato com Agendamento e Mensageria

- escolher Kafka ou RabbitMQ;
- aprovar envelope e tipos dos eventos;
- definir topico/exchange, chave e versao;
- definir estrategia para duplicidade e evento invalido.

**Aceite:** payload versionado e exemplos de `CREATED`/`UPDATED` aprovados.

### HIS-03 - Fechar o contrato de autenticacao

- definir como identidade e role chegam ao Historico;
- definir a relacao entre `userId` e `patientId`;
- validar matriz de permissao.

**Aceite:** cenarios 401, 403 e isolamento do paciente especificados.

### HIS-04 - Preparar o modulo

- configurar porta 8083;
- adicionar GraphQL e suporte de testes;
- configurar persistencia e migration;
- criar configuracao local sem credenciais versionadas;
- corrigir o teste de contexto.

**Aceite:** aplicacao sobe de forma reproduzivel e testes de contexto passam.

### HIS-05 - Implementar consultas GraphQL

- criar schema;
- criar DTOs de entrada e saida;
- criar resolver/controller;
- criar caso de uso de historico completo;
- criar filtro de consultas futuras.

**Aceite:** queries completa e futura passam nos testes GraphQL.

### HIS-06 - Implementar persistencia do read model

- criar entidade de persistencia separada do contrato GraphQL;
- criar repository;
- criar migration;
- garantir chave unica para `appointmentId` e `sourceEventId` conforme desenho aprovado.

**Aceite:** testes de integracao comprovam criacao, atualizacao, filtro e ordenacao.

### HIS-07 - Consumir eventos

- consumir `APPOINTMENT_CREATED`;
- consumir `APPOINTMENT_UPDATED`;
- validar payload;
- garantir idempotencia;
- implementar politica de retry/erro aprovada.

**Aceite:** testes com broker comprovam criacao, atualizacao e reentrega.

### HIS-08 - Aplicar autorizacao

- proteger endpoint GraphQL;
- aplicar autorizacao no caso de uso;
- limitar paciente aos proprios dados;
- permitir leitura para medico e enfermeiro;
- permitir edicao somente para medico.

**Aceite:** testes de seguranca cobrem sucesso, 401, 403 e isolamento.

### HIS-09 - Implementar edicao do historico

- escolher mutation GraphQL ou REST;
- validar campos editaveis;
- registrar auditoria da alteracao.

**Aceite:** medico edita; enfermeiro e paciente recebem acesso negado.

### HIS-10 - Documentar e integrar

- documentar schema e exemplos de query;
- adicionar exemplos ao Postman;
- documentar eventos e configuracao;
- validar fluxo ponta a ponta com Agendamento e Autenticacao.

**Aceite:** um integrante novo consegue executar e demonstrar o modulo usando apenas a documentacao.

## 12. Decisoes bloqueantes

| Decisao | Dependencia | Impacto se nao fechar |
| --- | --- | --- |
| Kafka ou RabbitMQ | Mensageria/equipe | Nao e possivel implementar o consumidor real |
| Forma de propagacao da identidade | Autenticacao/equipe | Nao e possivel garantir roles e isolamento do paciente |
| Campos e tipos do evento | Agendamento/Mensageria | Read model pode nascer incompativel |
| Significado e campos editaveis do historico | Equipe/professor | Nao e possivel cumprir com seguranca a edicao pelo medico |
| Timezone de `futureOnly` | Equipe | Resultados podem divergir entre ambientes |
| Banco do Historico | Arquitetura/equipe | Persistencia, migration e testes ficam indefinidos |

## 13. Definicao de pronto do History Service

O modulo estara pronto quando:

- os contratos bloqueantes estiverem documentados;
- consultas completa e futura funcionarem por GraphQL;
- permissoes forem aplicadas no caso de uso;
- paciente nao conseguir consultar outro paciente;
- criacao e edicao de consulta atualizarem o read model;
- reentrega nao duplicar efeito;
- testes unitarios, GraphQL, persistencia, seguranca e mensageria passarem;
- execucao local for reproduzivel;
- README e collection contiverem exemplos reais.
