# Discovery - History Service

## 1. Objetivo

Mapear o escopo do `history-service` antes da implementacao, reconciliando o enunciado oficial da FIAP, o documento complementar de Engenharia de Software, o Trello e a arquitetura atual do repositorio.

Este documento separa:

- **Confirmado**: exigido pela FIAP ou atribuido diretamente pelo grupo;
- **Proposta**: desenho tecnico recomendado para discussao;
- **Condicional**: depende de uma decisao arquitetural ainda nao tomada;
- **Pendente**: decisao que depende de outro integrante ou do grupo.

## 2. Fontes e precedencia

1. `ADJT - BB - Tech Challenge - Fase 3.pdf` - fonte normativa.
2. `Documento_Engenharia_Software_TechChallenge_Fase3 (1).pdf` - especificacao complementar.
3. Conversa com o responsavel pelo repositorio - distribuicao operacional do grupo.
4. Codigo da branch `main` - estado real implementado.

Em caso de conflito, prevalece o PDF oficial da FIAP.

## 3. Responsabilidade confirmada

A conversa com Joao confirma que Vinicius ficou com **Historico + GraphQL**. O PDF de Engenharia distribui "Seguranca e GraphQL" para um integrante generico, mas nao relaciona os numeros dos integrantes aos nomes da equipe. A distribuicao nominal do grupo e usada para separar as responsabilidades.

Dentro do `history-service`, Vinicius e responsavel por:

- contrato GraphQL do historico;
- consultas de historico completo e apenas futuro;
- regras de acesso aplicadas na borda do historico quando a identidade autenticada estiver disponivel;
- testes e documentacao do proprio modulo.

Sao responsabilidades condicionais, e nao atribuicoes confirmadas:

- persistencia ou banco proprio para o historico;
- consumo de eventos do Agendamento;
- implementacao da edicao do historico, pois os campos e o servico proprietario ainda nao foram definidos.

Nao fazem parte do escopo pessoal de Vinicius: implementar o servico de Autenticacao, publicar eventos do Agendamento, escolher/configurar o broker ou enviar notificacoes.

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

### HIS-RF-06 - Refletir criacoes e edicoes (condicional)

Se o `history-service` mantiver uma fonte propria, ela devera refletir consultas criadas e editadas no Servico de Agendamento.

O enunciado exige mensageria entre Agendamento e Notificacoes, nao entre Agendamento e Historico. Se o grupo tambem escolher integracao assincrona para alimentar o Historico, poderao ser processados eventos como:

- `APPOINTMENT_CREATED`;
- `APPOINTMENT_UPDATED`.

## 5. Requisitos nao funcionais confirmados

- Spring Boot e Java 21, conforme a stack adotada pelo grupo.
- GraphQL como contrato de consulta.
- controle de acesso por `MEDICO`, `ENFERMEIRO` e `PACIENTE` no sistema; a integracao local depende do contrato da Autenticacao;
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
    patientId: ID!
    futureOnly: Boolean! = false
  ): [AppointmentHistoryItem!]!
}

type AppointmentHistoryItem {
  appointmentId: ID!
  patientId: ID!
  doctorId: ID
  scheduledAt: String!
  status: String!
}
```

Regras propostas:

- `MEDICO` e `ENFERMEIRO`: informam `patientId` obrigatoriamente;
- `PACIENTE`: o servico deriva a identidade do usuario autenticado e rejeita tentativa de consultar outro paciente;
- resultado ordenado por `scheduledAt` de forma deterministica;
- `scheduledAt`: usar ISO-8601 com offset, por exemplo `2026-09-01T13:00:00-03:00`;
- `futureOnly = true`: comparar instantes e considerar apenas `scheduledAt > now`;
- ausencia de registros retorna lista vazia, nunca `null`.

A operacao de edicao pelo medico sera definida separadamente como mutation GraphQL ou endpoint REST depois que os campos editaveis forem aprovados.

## 8. Contrato de evento condicional

Este contrato nao faz parte da primeira entrega e so deve ser implementado se o grupo decidir alimentar o Historico por eventos. Nesse caso, deve ser aprovado com Agendamento e Mensageria antes da implementacao do consumidor:

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

### Primeira entrega - contrato GraphQL

- [x] Aplicacao inicia na porta 8083 sem depender de banco externo.
- [x] Query `appointmentHistory` recebe `patientId` obrigatorio.
- [x] `futureOnly = false` retorna todo o historico encontrado.
- [x] `futureOnly = true` retorna somente `scheduledAt > now`.
- [x] Ausencia de registros retorna `[]`.
- [x] Resultado possui ordenacao deterministica.
- [x] Testes unitarios, GraphQL e de contexto passam.

### Entrega posterior - autorizacao

- [ ] Medico consulta todo o historico de um paciente.
- [ ] Enfermeiro consulta todo o historico de um paciente.
- [ ] Paciente consulta o proprio historico.
- [ ] Paciente nao acessa historico de outro paciente.
- [ ] Usuario sem autenticacao recebe resposta de nao autenticado.
- [ ] Perfil sem permissao recebe resposta de acesso negado.

### Integracao assincrona (condicional)

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

**Responsabilidade:** equipe/Mensageria; nao bloqueia a primeira entrega do Historico.

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
- criar configuracao local sem credenciais versionadas;
- corrigir o teste de contexto.

Persistencia e migration entram somente depois da decisao de banco/read model proprio.

**Aceite:** aplicacao sobe de forma reproduzivel e testes de contexto passam.

### HIS-05 - Implementar consultas GraphQL

- criar schema;
- criar DTOs de entrada e saida;
- criar resolver/controller;
- criar caso de uso de historico completo;
- criar filtro de consultas futuras.

**Aceite:** queries completa e futura passam nos testes GraphQL.

### HIS-06 - Implementar persistencia do read model

**Condicional:** executar somente se a equipe aprovar persistencia propria para o Historico.

- criar entidade de persistencia separada do contrato GraphQL;
- criar repository;
- criar migration;
- garantir chave unica para `appointmentId` e `sourceEventId` conforme desenho aprovado.

**Aceite:** testes de integracao comprovam criacao, atualizacao, filtro e ordenacao.

### HIS-07 - Consumir eventos

**Condicional:** executar somente se a equipe aprovar a integracao Agendamento -> Historico por eventos.

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
| Kafka ou RabbitMQ | Mensageria/equipe | Bloqueia apenas um consumidor condicional; nao bloqueia GraphQL |
| Forma de propagacao da identidade | Autenticacao/equipe | Nao e possivel garantir roles e isolamento do paciente |
| Campos e tipos do evento | Agendamento/Mensageria | Read model pode nascer incompativel |
| Significado e campos editaveis do historico | Equipe/professor | Nao e possivel cumprir com seguranca a edicao pelo medico |
| Banco do Historico | Arquitetura/equipe | Persistencia, migration e testes ficam indefinidos |

## 13. Definicao de pronto do History Service

Para a primeira entrega, o modulo estara pronto quando:

- a aplicacao iniciar na porta 8083;
- as consultas completa e futura funcionarem por GraphQL;
- a borda GraphQL nao expuser o modelo de dominio diretamente;
- testes unitarios, GraphQL e de contexto passarem;
- o schema e a execucao local estiverem documentados.

Para a entrega final do modulo, ainda sera necessario, conforme os contratos da equipe:

- documentar os contratos bloqueantes;
- aplicar permissoes no caso de uso;
- impedir que o paciente consulte outro paciente;
- integrar a fonte real dos dados;
- testar persistencia e mensageria, somente se forem adotadas;
- manter a execucao local reproduzivel;
- fornecer exemplos reais no README e na collection.
