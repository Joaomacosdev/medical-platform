# Medical Platform no Postman

Para testar especificamente o vínculo entre os serviços, use a [collection Auth → Scheduling](auth-scheduling.integration.postman_collection.json), seu [ambiente](auth-scheduling.local.postman_environment.json) e o [guia de execução](AUTH_SCHEDULING_POSTMAN.md).

1. Importe medical-platform.postman_collection.json e medical-platform.local.postman_environment.json.
2. Selecione **Medical Platform - Local**, ajuste URLs e credenciais do RabbitMQ.
3. Execute as pastas em ordem pelo Runner (uma iteração).
4. Consulte **Test Results**: cenários negativos verificam erros esperados.

O [guia de integração](../AUTH_SCHEDULING.md) explica o fluxo e como vincular cadastros existentes.

## Cobertura e efeitos

63 requisições: verificação inicial da versão do scheduling, login/refresh no auth, cadastro de contas, permissões, cadastro local
idempotente, consultas REST/GraphQL, histórico, notificações e limpeza.

A pasta 02 completa os cadastros e captura IDs locais. Não use IDs fixos nem crie
variáveis de ambiente para IDs/tokens: elas sobrescreveriam os valores capturados
na collection. Datas são geradas no futuro.

A collection cria contas temporárias no auth, incluindo ADMIN, usando a API existente.
A limpeza cancela a consulta e remove essas contas. Cadastros locais e consulta cancelada permanecem no scheduling;
não existe exclusão automática entre bancos. Use um banco de testes.

Credenciais do seed scheduling não servem mais para login. Nenhuma credencial privada
do .env foi copiada. A collection específica do scheduling também foi atualizada.

As collections foram executadas contra os serviços locais. A integração específica passou; as collections mais amplas identificaram falhas existentes no auth. Veja [os resultados e diagnósticos](EXECUTION_RESULTS.md). Os testes preservam o comportamento esperado para não esconder essas falhas.

## Notificações e histórico

Criar/editar consulta publica eventos. Os contatos gerados usam example.com; configure
um destinatário de teste no cadastro do paciente para confirmar recebimento. Preencha
SMTP no ambiente da aplicação.

GET /ping verifica resposta do notification, não entrega de email. As consultas
RabbitMQ inspecionam email_queue/dlq_queue sem consumir mensagens. Confira logs e
destinatário para confirmar entrega.

History-service inicia vazio e continua sem integração com scheduling. Disponibilidade
pode retornar vazia: não há endpoint para cadastrar horários nem reserva automática.
