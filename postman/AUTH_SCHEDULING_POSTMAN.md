# Teste da integração Auth → Scheduling

## Como executar

1. Importe `auth-scheduling.integration.postman_collection.json` e `auth-scheduling.local.postman_environment.json`.
2. Selecione o ambiente **Integracao Auth - Scheduling Local**.
3. Ajuste `integration_auth_url` (padrão porta 8081) e `integration_scheduling_url` (8084).
4. Com auth, scheduling, MySQL e RabbitMQ disponíveis, execute a collection inteira pelo Runner, na ordem, com uma iteração.

O scheduling precisa estar com a integração e a migration V6 aplicadas. Os dois serviços precisam compartilhar a chave JWT e o scheduling deve aceitar o emissor `auth-service`. O auth mantém seu contrato atual, incluindo criação pública com `role`.

## O que as 37 requisições verificam

- Verificação inicial dos endpoints de cadastro; interrompe o Runner se o scheduling estiver desatualizado.

- Criação e login de um médico e dois pacientes no auth.
- Aceitação do access token no scheduling e identificação de cadastro pendente.
- Rejeição de refresh token e token adulterado nas APIs do scheduling.
- Cadastro local, captura de IDs e contato independente do email de login.
- Tentativa de enviar a identidade de outro paciente no cadastro: o servidor deve usar a identidade do token.
- Repetição do cadastro preservando o ID local; especialidade obrigatória para profissionais.
- Agendamento, confirmação e cancelamento usando IDs locais.
- Isolamento entre pacientes por ID, filtros REST e filtros GraphQL.
- Renovação no auth e acesso à mesma consulta com o novo token.
- Exclusão das três contas temporárias no auth.

IDs e tokens são capturados automaticamente nas variáveis da collection. Não crie variáveis de ambiente com os mesmos nomes `it_*`. A primeira requisição reinicia os dados de execução e gera nomes exclusivos; as datas ficam aproximadamente 30 dias no futuro. Para repetir uma requisição individual, execute antes suas dependências.

Os testes não presumem que IDs dos dois bancos sejam iguais ou diferentes: verificam o vínculo explícito e a identidade usada nas respostas. Erros esperados, como HTTP 401/403/409, contam como sucesso do teste.

## Efeitos e limites

Use um ambiente de testes. A collection cria cadastros e publica notificações ao criar/editar consultas. Os emails usam `example.com`; não é um teste de entrega SMTP.

A limpeza remove contas do auth e cancela a consulta. Cadastros locais e consulta cancelada permanecem no scheduling, que não oferece exclusão física nesse fluxo. Se interromper a execução, rode a pasta de limpeza antes de iniciar outra execução, enquanto os IDs e tokens anteriores ainda estiverem disponíveis.

A collection foi executada com Newman contra os serviços locais: 37 requisições e 84 verificações passaram. Consulte [os resultados](EXECUTION_RESULTS.md).

Se a primeira verificação falhar, execute `docker compose up -d --build --no-deps scheduling-service` e aguarde `GET http://localhost:8084/actuator/health` retornar status `UP` antes de repetir. Isso preserva os volumes dos bancos.

Para executar pelo terminal com Newman instalado:

```powershell
newman run postman/auth-scheduling.integration.postman_collection.json -e postman/auth-scheduling.local.postman_environment.json --timeout-request 10000
```
