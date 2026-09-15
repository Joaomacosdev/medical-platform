# Login único e cadastro de atendimento

## Responsabilidades e fluxo

O auth mantém contas, senhas e perfis. O scheduling mantém cadastro de atendimento,
contato, especialidade e consultas. Email de login e email de contato são independentes.

1. Crie uma conta PACIENTE com POST /api/users/v1 no auth.
2. Faça login JSON (email e password) em POST /api/auth/v1/login.
3. Use o access token em Authorization: Bearer TOKEN no scheduling.
4. Complete seu cadastro com PUT /api/v1/cadastro/me:

    {
      "nome": "Ana Silva",
      "emailContato": "ana.contato@example.com",
      "telefone": "11999999999",
      "especialidade": null
    }

Nome e email de contato são obrigatórios; MEDICO e ENFERMEIRO também precisam de
especialidade. O PUT retorna 200 e cria ou substitui o cadastro. Repetições preservam
seu ID local. Campos opcionais omitidos são apagados na substituição.
GET /api/v1/cadastro/me retorna o cadastro atual.

O perfil e authUserId vêm exclusivamente do token validado. Campos enviados pelo
cliente não podem escolher outra identidade. A resposta inclui id (local), authUserId,
nome, emailContato, telefone, especialidade e role.
Use o id local nos campos pacienteId/profissionalId de consultas.

O vínculo é JWT.sub → users.auth_user_id → users.id. IDs dos bancos não precisam coincidir.
Sem vínculo, as operações clínicas retornam HTTP 409 com code CADASTRO_PENDENTE.
GET do cadastro pendente retorna 409 com mensagem explicativa. Conflito de email ou
cadastro concorrente retorna 409: consulte o cadastro e tente novamente se necessário.

O scheduling só verifica tokens: valida assinatura, emissor, expiração, ID positivo,
email e perfil conhecido. Refresh tokens do auth não possuem os campos exigidos e são
rejeitados. Não há login Basic, emissão de tokens ou senhas no scheduling.
ADMIN não tem acesso às operações clínicas.

## Compatibilidade com o auth existente

Nenhuma alteração no auth-service é necessária. O JWT atual já contém sub, email e role.
O scheduling valida esse contrato e vincula o sub ao cadastro local.

Use POST /api/users/v1 e POST /api/auth/v1/login do auth existente para preparar as contas.
O comportamento atual do auth permite informar role no cadastro público, inclusive perfis
privilegiados. Esta integração preserva esse comportamento; não adiciona uma restrição
administrativa ao auth. ADMIN continua sem acesso às operações clínicas do scheduling.

A collection cria contas temporárias dos perfis necessários e as exclui no final.
Não exige administrador prévio ou alteração manual no banco do auth.

## Migração e cadastros legados

A migration V6 adiciona auth_user_id único e remove a coluna password do scheduling.
Preserva IDs locais, cadastros e consultas. As migrations V1–V5 não foram modificadas.
Cadastros antigos, inclusive os seeds, ficam com auth_user_id NULL.

Para reutilizar um cadastro, confira a identidade nos dois bancos e preencha o vínculo
por operação administrativa antes do primeiro PUT. Exemplo no banco do scheduling:

    UPDATE users SET auth_user_id = 7
    WHERE id = 42 AND auth_user_id IS NULL;

Esses números são exemplos. Confira se a conta 7 é a pessoa correta e tem perfil
compatível, e se ainda não está vinculada. Nunca vincule automaticamente por email
ou coincidência numérica. O índice único impede dois cadastros para a mesma identidade.

Email já usado por cadastro legado gera conflito: a API não assume a posse desse
cadastro. Faça o vínculo administrativo conferido ou use outro contato.
Ao trocar perfil no auth, faça novo login e atualize o cadastro para adequar os dados locais.

Excluir conta no auth não apaga consultas ou cadastro no scheduling.
Tokens já emitidos permanecem válidos até expirar; não foi adicionada revogação imediata.
Não reutilize IDs de identidades removidas.

## Configuração e testes

Os dois serviços precisam do mesmo JWT_SECRET. Scheduling usa JWT_ISSUER=auth-service.
O Compose da raiz fornece esses valores. Recrie as aplicações após o build; a V6 roda
no startup do scheduling. Não remova volumes para aplicar a mudança.

Na implantação, renove a chave compartilhada nos dois serviços e faça novo login para
invalidar tokens antigos emitidos pelo login local do scheduling com o mesmo emissor.
Os arquivos .env com credenciais não foram alterados por esta implementação.

Importe a collection e o ambiente em postman/ na raiz. Execute na ordem. As contas temporárias são criadas no auth e os IDs locais são capturados.

O history-service continua em memória. Disponibilidade continua separada do agendamento.
SMTP e RabbitMQ continuam necessários para entrega de email. Use um destinatário de
teste acessível; os contatos da collection são fictícios.

### Comandos de validação

- Scheduling (build e testes unitários/HTTP): mvn -f scheduling-service/pom.xml clean package
- Scheduling com MySQL/RabbitMQ temporários: mvn -f scheduling-service/pom.xml verify -Dapi.version=1.44
- Auth (a partir de auth-service/): mvn test "-Dtest=*Test"
- O teste AuthServiceApplicationTests exige o MySQL configurado para o auth.
- A opção api.version=1.44 contorna a incompatibilidade do cliente Testcontainers 1.20.4
  com a API mínima do Docker 29, sem alterar o runtime da aplicação.
