# Notification Service

Servico responsavel por consumir eventos de consultas pelo RabbitMQ e enviar notificacoes por e-mail via SMTP. Nao utiliza banco de dados.

## Arquitetura e fluxo

```text
Produtor de eventos (servico de agendamento)
    -> medical_exchange (Fanout)
        -> email_queue
            -> EmailConsumer
                -> SendEmailNotificationService
                    -> NotificationSender (contrato)
                        -> SmtpEmailSender -> SMTP

Sucesso no envio -> ACK
Falha no processamento -> NACK sem requeue
    -> dlq_medical_exchange (Direct) -> dql_queue
```

O consumidor controla ACK/NACK. A camada de aplicacao valida os dados e monta o texto da consulta. A implementacao SMTP utiliza `JavaMailSender`. O ACK confirma o processamento no RabbitMQ apos o SMTP aceitar o envio; nao comprova leitura ou entrega final na caixa do destinatario.

## Endpoint HTTP

| Metodo | Caminho | Resposta esperada |
| --- | --- | --- |
| GET | `/ping` | HTTP 200 com corpo `pong` |

Importe [a collection Postman](docs/postman/notification-service.postman_collection.json) e ajuste `baseUrl`. A collection verifica o status e o corpo do health check. O recebimento de notificacoes acontece pelo RabbitMQ; este servico nao expoe endpoint HTTP de criacao ou edicao de consultas. As chamadas desses casos pertencem a collection do servico de agendamento.

## Contrato de integracao

| Configuracao | Valor |
| --- | --- |
| Exchange de entrada | `medical_exchange` |
| Tipo | `fanout` |
| Routing key convencionada | `medical.notification` (nao filtra mensagens em fanout) |
| Fila consumida | `email_queue` |
| Content type | `application/json` |
| Exchange de falhas | `dlq_medical_exchange` |
| Routing key de falhas | `dlq.notification` |
| Fila de falhas | `dql_queue` |

O produtor deve publicar os dados atuais quando uma consulta for criada ou editada. O contrato atual nao possui discriminador de tipo de evento: ambos os casos usam o mesmo formato e texto de e-mail.

```json
{
  "patientId": 1,
  "patientEmail": "paciente@example.com",
  "patientNumber": null,
  "consultation": {
    "consultationId": 100,
    "doctorName": "Dra. Ana",
    "medicalSpecialty": "Cardiologia",
    "consultationDate": "2027-06-20T14:30:00"
  }
}
```

Use um destinatario sob seu controle e uma data futura ao executar o teste. O endereco acima e ilustrativo.

| Campo | Uso e validacao atual |
| --- | --- |
| `patientId` | Identificador do paciente; nao utilizado no envio atual. |
| `patientEmail` | Destinatario; nao pode ser nulo ou vazio. A aplicacao nao possui validacao propria completa do formato do e-mail. |
| `patientNumber` | Telefone reservado para evolucao; pode ser nulo, nao ha envio SMS. |
| `consultation` | Necessario para extrair os dados do e-mail. |
| `consultation.consultationId` | Identificador da consulta; nao utilizado no envio atual. |
| `consultation.doctorName` | Nome do profissional; nao pode ser nulo ou vazio. |
| `consultation.medicalSpecialty` | Especialidade; nao pode ser nula ou vazia. |
| `consultation.consultationDate` | Data obrigatoria em formato ISO local, sem offset; produtor e consumidor devem alinhar o fuso. Ainda nao ha rejeicao de datas passadas. |

## Validacao manual e resultados

Validacao informada pelo desenvolvedor: foi realizado um envio real para outra conta de e-mail propria e o recebimento foi confirmado. Esse relato registra a entrega SMTP; nao substitui evidencia de criacao e edicao pelos endpoints do agendamento. Nao foram anexados capturas de tela ou identificadores dessa execucao.

### Reproduzir o teste de notificacao

1. Inicie RabbitMQ e a aplicacao com as credenciais SMTP configuradas.
2. No painel RabbitMQ, abra **Exchanges -> medical_exchange -> Publish message**.
3. Informe `medical.notification` como routing key, `application/json` em content type e o JSON acima como payload. Para persistencia da mensagem no broker, use delivery mode `2`.
4. Publique a mensagem e confira a caixa de entrada e a pasta de spam do destinatario.
5. Confira assunto, medico, especialidade e data. Verifique a fila `email_queue` e se houve mensagens novas na `dql_queue`; uma fila vazia, sozinha, nao comprova a entrega do e-mail.
6. Para testar dados alterados, publique o mesmo identificador de consulta com outro horario e confira o novo e-mail. Isso valida o consumidor; a integracao completa exige que a edicao no agendamento produza esse evento.

Exemplo ilustrativo do e-mail esperado para o payload acima:

```text
Assunto: Notificação de consulta

Olá!

Confira os dados da sua consulta:
Médico(a): Dra. Ana
Especialidade: Cardiologia
Data e horário: 20/06/2027 14:30

Equipe Medical
```

| Cenario | Resultado esperado |
| --- | --- |
| Evento valido | Envio aceito pelo SMTP, seguido de ACK; conferir recebimento no destinatario. |
| Evento com horario alterado | Novo e-mail com o horario atualizado. |
| Destinatario vazio | Nenhum envio; NACK sem requeue e encaminhamento para DLQ. |
| SMTP indisponivel ou autenticacao recusada | NACK sem requeue e encaminhamento para DLQ. |

### Evidencias da integracao completa

Na entrega do sistema, incluir as requisicoes de criacao e edicao na collection do agendamento. Registrar para cada execucao o identificador da consulta, o horario do teste, o resultado HTTP e o e-mail recebido com os dados correspondentes. Usar dados ficticios de paciente e ocultar credenciais e enderecos pessoais nas capturas.

### Falhas e reprocessamento

Nao ha retry automatico. Corrigir a causa de uma falha nao retira a mensagem da DLQ. Em um teste controlado, inspecione a mensagem na `dql_queue`, corrija os dados ou a configuracao e republique o JSON na exchange de entrada com content type `application/json`. Trate explicitamente a copia retida na DLQ para nao repetir o reprocessamento.

Nao ha deduplicacao de eventos. Se o SMTP aceitar o envio e ocorrer uma falha antes do ACK, uma reentrega pode produzir outro e-mail. Inspecione a execucao anterior antes de republicar.

## Executar com Docker

Requer Docker com suporte a containers Linux e Docker Compose.

1. Copie `.env.example` para `.env`.
2. Defina o usuario e a senha do RabbitMQ. Para Gmail, configure `SMTP_USERNAME` com o endereco completo e `SMTP_PASSWORD` com uma senha de app da mesma conta. Use esse endereco em `SMTP_FROM`.
3. Execute `docker compose up --build -d`.
4. Consulte `http://localhost:8080/ping` e o painel RabbitMQ em `http://localhost:15672`.

```powershell
Copy-Item .env.example .env
# Edite .env antes de iniciar.
docker compose up --build -d
docker compose logs -f app
docker compose down
```

As portas publicadas podem ser alteradas por `APP_PORT`, `RABBITMQ_AMQP_PORT` e `RABBITMQ_MANAGEMENT_PORT`. Se ja houver RabbitMQ ou uma aplicacao local usando essas portas, pare o processo anterior ou escolha portas livres. Dentro do Compose, a aplicacao usa `rabbitmq:5672`.

O volume `rabbitmq_data` preserva as filas ao parar os containers. As variaveis de usuario e senha inicializam um broker novo; alterar `.env` nao modifica automaticamente usuarios em um volume existente.

As credenciais entram apenas na execucao, por variaveis de ambiente. `.env` e ignorado pelo Git e pelo build Docker. Para valores contendo `$` ou `#`, use aspas simples no `.env`. O Dockerfile usa Java 21 e executa a aplicacao com usuario sem privilegios de root.

O endpoint `/ping` indica que o HTTP esta respondendo, nao valida credenciais SMTP. Para validar o fluxo, publique um JSON com o contrato `NotificationMessageDTO` na `email_queue`, usando content type `application/json`. O consumidor envia o e-mail, confirma a mensagem em caso de sucesso e rejeita sem requeue em caso de falha, encaminhando para `dql_queue`.

## Executar pela IDE

Configure as variaveis de ambiente na configuracao de execucao. O Spring nao carrega `.env` automaticamente. Use `RABBITMQ_HOST=localhost`, a porta publicada em `RABBITMQ_PORT`, e as mesmas credenciais em `RABBITMQ_USERNAME` e `RABBITMQ_PASSWORD`.

O build da imagem pula os testes. Execute `mvn test` separadamente; o teste de contexto desativa os listeners RabbitMQ e os testes de envio usam SMTP simulado, sem depender de credenciais reais.
