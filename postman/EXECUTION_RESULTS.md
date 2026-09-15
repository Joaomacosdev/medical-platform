# Execução real das collections

Executadas em 15/09/2026 com Newman contra os serviços locais. Resultados estruturados, sem tokens ou senhas: [execution-results.json](execution-results.json).

| Collection | Requisições | Verificações aprovadas | Falhas de verificação |
|---|---:|---:|---:|
| Auth → Scheduling | 37 | 84/84 | 0 |
| Medical Platform | 63 | 111/115 | 4 |
| Scheduling com preparação no auth | 58 | 97/101 | 4 |

Todas as requisições da execução final receberam resposta HTTP. As falhas nas duas últimas collections correspondem aos mesmos três problemas do auth; o login inválido causa duas verificações reprovadas.

## Causa da falha inicial da integração

O container local do scheduling executava a versão anterior: GET/PUT /api/v1/cadastro/me retornavam 404, e a listagem não exigia cadastro vinculado. O código novo no workspace não atualiza automaticamente uma imagem Docker já em execução.

Foi executado:

```powershell
docker compose up -d --build --no-deps scheduling-service
```

Os volumes foram preservados. Após a inicialização da versão nova, o fluxo completo Auth → Scheduling passou, incluindo cadastro idempotente, bloqueio de identidade enviada no corpo, isolamento entre pacientes, GraphQL, refresh e cancelamento.

As três collections agora começam verificando se o OpenAPI do scheduling contém GET/PUT /api/v1/cadastro/me. Se não contiver, o Runner é interrompido antes de criar contas. Aguarde /actuator/health retornar UP ao reconstruir a aplicação.

## Problemas encontrados no auth existente

1. **Atualizar perfil com o próprio email retorna 409.** UpdateProfileUserUseCase verifica se o email existe, sem excluir o próprio usuário.
2. **Cadastro inválido retorna 401 em vez de 400.** O comportamento foi reproduzido com corpo vazio. A causa interna desse tratamento de erro não foi alterada nesta tarefa.
3. **Troca de senha retorna 200, mas o novo login retorna 401.** O código aplica PasswordEncoder em UpdatePasswordUserUseCase e novamente em UserJpaAdapter.save, resultando em hash duplo. O adapter também recodifica a senha ao salvar outras alterações do usuário.

Os testes mantêm as respostas esperadas e continuam revelando essas falhas. O auth-service não foi alterado, conforme a restrição do usuário. A collection específica da integração não depende de atualização de perfil/senha no auth e passou integralmente.

## Efeitos dos testes

As contas temporárias foram removidas pelas pastas de limpeza. Cadastros locais e consultas canceladas permanecem no scheduling conforme seu contrato. Criação/edição de consultas publicou eventos; estes testes não comprovam entrega SMTP. Nenhum commit ou push foi realizado.
