
Conversa com o Gemini
converta em markdown

Aqui está o documento convertido inteiramente para a linguagem Markdown, preservando a estrutura, títulos, tabelas, listagens e conteúdo original do texto:  
PDF

DOCUMENTO DE ENGENHARIA DE SOFTWARE
Sistema Hospitalar para Agendamento de Consultas e Gerenciamento de Histórico Médico
Projeto: Tech Challenge - Fase 3

Data: Agosto de 2026

Sistema Hospitalar - Backend Modular em Microsserviços

  
PDF

Sumário
Introdução

1.1 Contextualização

1.2 Objetivo

1.3 Escopo

Processo de Desenvolvimento

2.1 Metodologia

2.2 Arquitetura

2.3 Tecnologias

2.4 Tecnologias Utilizadas

2.5 Divisão das Atividades da Equipe

Requisitos

3.1 Requisitos Funcionais

3.2 Requisitos Não Funcionais

Técnicas de Levantamento de Requisitos

UML - Introdução

Diagrama de Casos de Uso

Documentação dos Casos de Uso

UC01 - Autenticar Usuário

UC02 - Registrar Consulta

UC03 - Editar Consulta

UC04 - Consultar Histórico

UC05 - Visualizar Consultas

UC06 - Enviar Lembrete

Considerações Finais

1. Introdução
1.1 Contextualização
O setor da saúde exige sistemas confiáveis, capazes de organizar consultas médicas, armazenar históricos clínicos e permitir comunicação eficiente entre profissionais e pacientes. Além disso, a segurança das informações é indispensável em razão da natureza sensível dos dados médicos.  
PDF
+ 1

O Tech Challenge propõe o desenvolvimento de um backend para um sistema hospitalar modular, utilizando autenticação, autorização, comunicação assíncrona e arquitetura baseada em serviços. O sistema deve atender a diferentes perfis de usuários, garantindo que cada um tenha acesso apenas às funcionalidades permitidas.  
PDF
+ 1

1.2 Objetivo
Desenvolver uma aplicação backend segura, escalável e modular que permita:  
PDF

Gerenciamento de consultas médicas;  
PDF

Consulta ao histórico dos pacientes;  
PDF

Envio automático de lembretes;  
PDF

Autenticação e autorização dos usuários;  
PDF

Comunicação assíncrona entre serviços.  
PDF

1.3 Escopo
O sistema contempla:  
PDF

Cadastro de consultas;  
PDF

Alteração de consultas;  
PDF

Histórico médico;  
PDF

Consulta via GraphQL;  
PDF

Controle de acesso por perfil;  
PDF

Comunicação entre microsserviços utilizando RabbitMQ ou Kafka;  
PDF

Serviço de notificações.  
PDF

2. Processo de Desenvolvimento
2.1 Metodologia
Foi adotada uma metodologia ágil baseada em Scrum.  
PDF

Papéis:

  
PDF

Product Owner;  
PDF

Scrum Master;  
PDF

Equipe de Desenvolvimento.  
PDF

Cerimônias:

  
PDF

Sprint Planning;  
PDF

Daily Meeting;  
PDF

Sprint Review;  
PDF

Sprint Retrospective.  
PDF

2.2 Arquitetura
A solução foi estruturada em microsserviços, conforme descrito a seguir.  
PDF

Serviço de Agendamento:
Responsável por criar consultas, editar consultas e publicar eventos para notificação.  
PDF

Serviço de Notificações:
Responsável por consumir mensagens da fila e enviar lembretes aos pacientes.  
PDF

Serviço de Histórico (Opcional):
Responsável por armazenar histórico médico e disponibilizar consultas por meio do GraphQL.  
PDF

Essa separação favorece a escalabilidade, o baixo acoplamento e a facilidade de manutenção da solução.  
PDF

2.3 Tecnologias
Stack tecnológica principal adotada no projeto:  
PDF

Java  
PDF

Spring Boot  
PDF

Spring Security  
PDF

GraphQL  
PDF

RabbitMQ ou Kafka  
PDF

PostgreSQL  
PDF

Maven  
PDF

Docker  
PDF

GitHub  
PDF

2.4 Tecnologias Utilizadas
Para o desenvolvimento do sistema hospitalar serão utilizadas tecnologias modernas, amplamente empregadas em aplicações corporativas, garantindo segurança, escalabilidade, manutenibilidade e alto desempenho.  
PDF

Tecnologia	Finalidade
Java 21	Linguagem principal utilizada no desenvolvimento do backend.
Spring Boot	Framework para construção da API REST e configuração da aplicação.
Spring Security	Autenticação e autorização baseadas em perfis de usuários (Médico, Enfermeiro e Paciente).
Spring Data JPA	Camada de persistência responsável pela comunicação com o banco de dados.
Hibernate	Framework ORM utilizado pelo Spring Data JPA para mapear objetos em tabelas relacionais.
GraphQL	Consulta flexível ao histórico de consultas e informações dos pacientes.
RabbitMQ (ou Kafka)	Comunicação assíncrona entre os microsserviços, permitindo o envio de eventos ao serviço de notificações.
PostgreSQL	Banco de dados relacional utilizado para armazenar usuários, consultas e histórico médico.
Docker	Containerização da aplicação e dos serviços, facilitando implantação e execução.
Maven	Gerenciamento de dependências e automação do processo de build.
JUnit	Desenvolvimento de testes unitários.
Mockito	Criação de objetos simulados (mocks) para testes unitários.
Postman	Testes e validação dos endpoints da API e das consultas GraphQL.
Git	Controle de versão do código-fonte.
GitHub	Hospedagem do repositório e colaboração entre os integrantes da equipe.
Swagger/OpenAPI	Documentação interativa dos endpoints da API REST.
2.5 Divisão das Atividades da Equipe
O desenvolvimento do projeto será realizado por uma equipe composta por cinco integrantes. A divisão das atividades foi planejada para distribuir as responsabilidades de forma equilibrada, permitindo que cada integrante atue em uma área específica do sistema e contribua para a integração final da solução.  
PDF
+ 1

Integrante	Responsabilidade	Atividades
Integrante 1	Líder Técnico / Arquiteto	Definição da arquitetura, configuração inicial do projeto, organização do repositório, revisão de código, integração dos microsserviços e apoio técnico à equipe.
Integrante 2	Backend - Agendamento	Desenvolvimento das funcionalidades de cadastro, edição, cancelamento e consulta de consultas; criação dos endpoints REST e integração com o banco de dados.
Integrante 3	Backend - Segurança e GraphQL	Implementação do Spring Security, autenticação, autorização por perfis (Médico, Enfermeiro e Paciente) e desenvolvimento das consultas GraphQL para o histórico médico.
Integrante 4	Microsserviços e Mensageria	Implementação do RabbitMQ, desenvolvimento do Serviço de Notificações, publicação e consumo de eventos, além da comunicação assíncrona entre os serviços.
Integrante 5	Qualidade e Documentação	Desenvolvimento dos testes unitários e de integração, criação da documentação técnica (Swagger), elaboração da collection do Postman, documentação de Engenharia de Software e apoio na validação final do projeto.
3. Requisitos
3.1 Requisitos Funcionais
Código	Descrição
RF01	O sistema deverá permitir a autenticação de usuários.
RF02	O sistema deverá controlar permissões conforme o perfil do usuário.
RF03	Médicos poderão visualizar e editar históricos médicos.
RF04	Enfermeiros poderão registrar consultas.
RF05	Pacientes poderão visualizar apenas suas próprias consultas.
RF06	O sistema deverá permitir consultas utilizando GraphQL.
RF07	O sistema deverá permitir o cadastro de consultas.
RF08	O sistema deverá permitir a edição de consultas.
RF09	Após o cadastro de uma consulta, deverá ser enviada uma mensagem ao serviço de notificações.
RF10	O serviço de notificações deverá enviar lembretes automaticamente.
Esses requisitos refletem diretamente as necessidades apresentadas no desafio.

  
PDF

3.2 Requisitos Não Funcionais
Código	Descrição
RNF01	A autenticação deverá utilizar Spring Security.
RNF02	A comunicação entre serviços deverá ocorrer de forma assíncrona.
RNF03	O sistema deverá possuir arquitetura modular.
RNF04	Os serviços deverão ser desacoplados entre si.
RNF05	O código deverá seguir boas práticas de organização.
RNF06	A documentação deverá conter os endpoints e as instruções de execução.
Esses requisitos estão alinhados aos critérios de qualidade e documentação exigidos pelo Tech Challenge.

  
PDF

4. Técnicas de Levantamento de Requisitos
Embora o desafio original não especifique uma técnica de elicitação, recomenda-se, para este projeto, a aplicação das seguintes práticas de Engenharia de Software:  
PDF

Entrevistas: Realizadas com médicos, enfermeiros, administradores e pacientes, com o objetivo de compreender as necessidades de cada perfil.  
PDF

Observação: Análise de como ocorre atualmente o processo de agendamento das consultas.  
PDF

Brainstorming: Reuniões entre a equipe técnica e especialistas do hospital para identificar oportunidades de melhoria.  
PDF

Análise Documental: Consulta às normas internas do hospital e aos requisitos legais referentes à proteção de dados dos pacientes.  
PDF

Protótipos: Construção de protótipos das APIs e dos fluxos de atendimento para validação dos requisitos junto aos usuários.  
PDF

5. UML - Introdução
A UML (Unified Modeling Language) é uma linguagem padrão para modelagem de sistemas orientados a objetos. Seu objetivo é representar visualmente os elementos do software antes da implementação.  
PDF
+ 1

Para este projeto, os principais diagramas que poderiam ser utilizados são:  
PDF

Casos de Uso;  
PDF

Classes;  
PDF

Sequência;  
PDF

Componentes;  
PDF

Implantação.  
PDF

Neste documento é detalhado o Diagrama de Casos de Uso.  
PDF

6. Diagrama de Casos de Uso
Atores:

  
PDF

Médico  
PDF

Enfermeiro  
PDF

Paciente  
PDF

Sistema de Notificações  
PDF

Casos de Uso:

  
PDF

Autenticar usuário  
PDF

Registrar consulta  
PDF

Editar consulta  
PDF

Consultar histórico  
PDF

Consultar próximas consultas  
PDF

Visualizar consultas  
PDF

Enviar lembrete  
PDF

Relação Ator × Caso de Uso
Ator	Caso de Uso
Médico	Registrar Consulta
Médico	Editar Consulta
Médico	Consultar Histórico
Enfermeiro	Registrar Consulta
Enfermeiro	Consultar Histórico
Paciente	Visualizar Consultas
Paciente	Consultar Próximas Consultas
Sistema de Notificações	Enviar Lembretes
7. Documentação dos Casos de Uso
UC01 - Autenticar Usuário
Objetivo: Permitir o acesso seguro ao sistema.  
PDF

Atores: Médico, Enfermeiro e Paciente.  
PDF

Pré-condição: Usuário previamente cadastrado.  
PDF

Fluxo Principal:

Informar usuário.  
PDF

Informar senha.  
PDF

Validar credenciais.  
PDF

Liberar acesso.  
PDF

Pós-condição: Usuário autenticado.  
PDF

UC02 - Registrar Consulta
Ator: Médico ou Enfermeiro.  
PDF

Fluxo:

Selecionar paciente.  
PDF

Informar data.  
PDF

Informar horário.  
PDF

Salvar consulta.  
PDF

Publicar evento para RabbitMQ/Kafka.  
PDF

Pós-condição: Consulta cadastrada.  
PDF

UC03 - Editar Consulta
Ator: Médico.  
PDF

Fluxo:

Localizar consulta.  
PDF

Alterar dados.  
PDF

Salvar alterações.  
PDF

Publicar evento para notificações.  
PDF

UC04 - Consultar Histórico
Atores: Médico e Enfermeiro.  
PDF

Fluxo:

Selecionar paciente.  
PDF

Sistema consulta o histórico via GraphQL.  
PDF

Histórico é retornado.  
PDF

UC05 - Visualizar Consultas
Ator: Paciente.  
PDF

Fluxo:

Login.  
PDF

Consultar agenda.  
PDF

Sistema retorna apenas as consultas do paciente.  
PDF

UC06 - Enviar Lembrete
Ator: Sistema de Notificações.  
PDF

Fluxo:

Receber mensagem.  
PDF

Processar evento.  
PDF

Identificar paciente.  
PDF

Enviar lembrete automaticamente.  
PDF

8. Considerações Finais
O sistema hospitalar proposto atende aos requisitos apresentados no Tech Challenge ao utilizar autenticação com Spring Security, controle de acesso baseado em perfis, consultas por GraphQL, comunicação assíncrona por RabbitMQ ou Kafka e arquitetura modular baseada em microsserviços.  
PDF

A documentação desenvolvida aplica práticas de Engenharia de Software para organizar os requisitos, descrever os processos de desenvolvimento e especificar os principais casos de uso, facilitando a implementação, a manutenção e a evolução do sistema.  
PDF

As informações específicas do desafio foram extraídas do material fornecido, enquanto a estrutura de engenharia foi elaborada para atender aos padrões acadêmicos de documentação.  
PDF

Documento elaborado para o Tech Challenge - Fase 3 - Engenharia de Software

  
PDF

gere um arquivo

Seus arquivos em Markdown e PDF estão prontos:

Ícone do app MD
Documento_Engenharia_Software_TechChallenge_Fase3
 MD 
Ícone do app PDF
Documento_Engenharia_Software_TechChallenge_Fase3
 PDF 
Foram gerados dois formatos para o documento de Engenharia de Software:

Arquivo Markdown (.md): Contendo a formatação completa em texto puro (tabelas, títulos e marcações).

Arquivo PDF (.pdf): Uma versão estruturada e diagramada a partir do Markdown.




O Gemini é uma IA e pode cometer erros.

Analisando
# DOCUMENTO DE ENGENHARIA DE SOFTWARE
## Sistema Hospitalar para Agendamento de Consultas e Gerenciamento de Histórico Médico
**Projeto:** Tech Challenge - Fase 3  
**Data:** Agosto de 2026  
**Sistema Hospitalar - Backend Modular em Microsserviços**

---

## Sumário
1. [Introdução](#1-introdução)
   - 1.1 [Contextualização](#11-contextualização)
   - 1.2 [Objetivo](#12-objetivo)
   - 1.3 [Escopo](#13-escopo)
2. [Processo de Desenvolvimento](#2-processo-de-desenvolvimento)
   - 2.1 [Metodologia](#21-metodologia)
   - 2.2 [Arquitetura](#22-arquitetura)
   - 2.3 [Tecnologias](#23-tecnologias)
   - 2.4 [Tecnologias Utilizadas](#24-tecnologias-utilizadas)
   - 2.5 [Divisão das Atividades da Equipe](#25-divisão-das-atividades-da-equipe)
3. [Requisitos](#3-requisitos)
   - 3.1 [Requisitos Funcionais](#31-requisitos-funcionais)
   - 3.2 [Requisitos Não Funcionais](#32-requisitos-não-funcionais)
4. [Técnicas de Levantamento de Requisitos](#4-técnicas-de-levantamento-de-requisitos)
5. [UML - Introdução](#5-uml---introdução)
6. [Diagrama de Casos de Uso](#6-diagrama-de-casos-de-uso)
7. [Documentação dos Casos de Uso](#7-documentação-dos-casos-de-uso)
   - [UC01 - Autenticar Usuário](#uc01---autenticar-usuário)
   - [UC02 - Registrar Consulta](#uc02---registrar-consulta)
   - [UC03 - Editar Consulta](#uc03---editar-consulta)
   - [UC04 - Consultar Histórico](#uc04---consultar-histórico)
   - [UC05 - Visualizar Consultas](#uc05---visualizar-consultas)
   - [UC06 - Enviar Lembrete](#uc06---enviar-lembrete)
8. [Considerações Finais](#8-considerações-finais)

---

## 1. Introdução

### 1.1 Contextualização
O setor da saúde exige sistemas confiáveis, capazes de organizar consultas médicas, armazenar históricos clínicos e permitir comunicação eficiente entre profissionais e pacientes. Além disso, a segurança das informações é indispensável em razão da natureza sensível dos dados médicos.

O Tech Challenge propõe o desenvolvimento de um backend para um sistema hospitalar modular, utilizando autenticação, autorização, comunicação assíncrona e arquitetura baseada em serviços. O sistema deve atender a diferentes perfis de usuários, garantindo que cada um tenha acesso apenas às funcionalidades permitidas.

### 1.2 Objetivo
Desenvolver uma aplicação backend segura, escalável e modular que permita:
- Gerenciamento de consultas médicas;
- Consulta ao histórico dos pacientes;
- Envio automático de lembretes;
- Autenticação e autorização dos usuários;
- Comunicação assíncrona entre serviços.

### 1.3 Escopo
O sistema contempla:
- Cadastro de consultas;
- Alteração de consultas;
- Histórico médico;
- Consulta via GraphQL;
- Controle de acesso por perfil;
- Comunicação entre microsserviços utilizando RabbitMQ ou Kafka;
- Serviço de notificações.

---

## 2. Processo de Desenvolvimento

### 2.1 Metodologia
Foi adotada uma metodologia ágil baseada em Scrum.

**Papéis:**
- Product Owner;
- Scrum Master;
- Equipe de Desenvolvimento.

**Cerimônias:**
- Sprint Planning;
- Daily Meeting;
- Sprint Review;
- Sprint Retrospective.

### 2.2 Arquitetura
A solução foi estruturada em microsserviços, conforme descrito a seguir.

* **Serviço de Agendamento:**
  Responsável por criar consultas, editar consultas e publicar eventos para notificação.
* **Serviço de Notificações:**
  Responsável por consumir mensagens da fila e enviar lembretes aos pacientes.
* **Serviço de Histórico (Opcional):**
  Responsável por armazenar histórico médico e disponibilizar consultas por meio do GraphQL.

Essa separação favorece a escalabilidade, o baixo acoplamento e a facilidade de manutenção da solução.

### 2.3 Tecnologias
Stack tecnológica principal adotada no projeto:
- Java
- Spring Boot
- Spring Security
- GraphQL
- RabbitMQ ou Kafka
- PostgreSQL
- Maven
- Docker
- GitHub

### 2.4 Tecnologias Utilizadas
Para o desenvolvimento do sistema hospitalar serão utilizadas tecnologias modernas, amplamente empregadas em aplicações corporativas, garantindo segurança, escalabilidade, manutenibilidade e alto desempenho.

| Tecnologia | Finalidade |
| :--- | :--- |
| **Java 21** | Linguagem principal utilizada no desenvolvimento do backend. |
| **Spring Boot** | Framework para construção da API REST e configuração da aplicação. |
| **Spring Security** | Autenticação e autorização baseadas em perfis de usuários (Médico, Enfermeiro e Paciente). |
| **Spring Data JPA** | Camada de persistência responsável pela comunicação com o banco de dados. |
| **Hibernate** | Framework ORM utilizado pelo Spring Data JPA para mapear objetos em tabelas relacionais. |
| **GraphQL** | Consulta flexível ao histórico de consultas e informações dos pacientes. |
| **RabbitMQ (ou Kafka)** | Comunicação assíncrona entre os microsserviços, permitindo o envio de eventos ao serviço de notificações. |
| **PostgreSQL** | Banco de dados relacional utilizado para armazenar usuários, consultas e histórico médico. |
| **Docker** | Containerização da aplicação e dos serviços, facilitando implantação e execução. |
| **Maven** | Gerenciamento de dependências e automação do processo de build. |
| **JUnit** | Desenvolvimento de testes unitários. |
| **Mockito** | Criação de objetos simulados (mocks) para testes unitários. |
| **Postman** | Testes e validação dos endpoints da API e das consultas GraphQL. |
| **Git** | Controle de versão do código-fonte. |
| **GitHub** | Hospedagem do repositório e colaboração entre os integrantes da equipe. |
| **Swagger/OpenAPI** | Documentação interativa dos endpoints da API REST. |

### 2.5 Divisão das Atividades da Equipe
O desenvolvimento do projeto será realizado por uma equipe composta por cinco integrantes. A divisão das atividades foi planejada para distribuir as responsabilidades de forma equilibrada, permitindo que cada integrante atue em uma área específica do sistema e contribua para a integração final da solução.

| Integrante | Responsabilidade | Atividades |
| :--- | :--- | :--- |
| **Integrante 1** | Líder Técnico / Arquiteto | Definição da arquitetura, configuração inicial do projeto, organização do repositório, revisão de código, integração dos microsserviços e apoio técnico à equipe. |
| **Integrante 2** | Backend - Agendamento | Desenvolvimento das funcionalidades de cadastro, edição, cancelamento e consulta de consultas; criação dos endpoints REST e integração com o banco de dados. |
| **Integrante 3** | Backend - Segurança e GraphQL | Implementação do Spring Security, autenticação, autorização por perfis (Médico, Enfermeiro e Paciente) e desenvolvimento das consultas GraphQL para o histórico médico. |
| **Integrante 4** | Microsserviços e Mensageria | Implementação do RabbitMQ, desenvolvimento do Serviço de Notificações, publicação e consumo de eventos, além da comunicação assíncrona entre os serviços. |
| **Integrante 5** | Qualidade e Documentação | Desenvolvimento dos testes unitários e de integração, criação da documentação técnica (Swagger), elaboração da collection do Postman, documentação de Engenharia de Software e apoio na validação final do projeto. |

---

## 3. Requisitos

### 3.1 Requisitos Funcionais

| Código | Descrição |
| :--- | :--- |
| **RF01** | O sistema deverá permitir a autenticação de usuários. |
| **RF02** | O sistema deverá controlar permissões conforme o perfil do usuário. |
| **RF03** | Médicos poderão visualizar e editar históricos médicos. |
| **RF04** | Enfermeiros poderão registrar consultas. |
| **RF05** | Pacientes poderão visualizar apenas suas próprias consultas. |
| **RF06** | O sistema deverá permitir consultas utilizando GraphQL. |
| **RF07** | O sistema deverá permitir o cadastro de consultas. |
| **RF08** | O sistema deverá permitir a edição de consultas. |
| **RF09** | Após o cadastro de uma consulta, deverá ser enviada uma mensagem ao serviço de notificações. |
| **RF10** | O serviço de notificações deverá enviar lembretes automaticamente. |

### 3.2 Requisitos Não Funcionais

| Código | Descrição |
| :--- | :--- |
| **RNF01** | A autenticação deverá utilizar Spring Security. |
| **RNF02** | A comunicação entre serviços deverá ocorrer de forma assíncrona. |
| **RNF03** | O sistema deverá possuir arquitetura modular. |
| **RNF04** | Os serviços deverão ser desacoplados entre si. |
| **RNF05** | O código deverá seguir boas práticas de organização. |
| **RNF06** | A documentação deverá conter os endpoints e as instruções de execução. |

---

## 4. Técnicas de Levantamento de Requisitos
Embora o desafio original não especifique uma técnica de elicitação, recomenda-se, para este projeto, a aplicação das seguintes práticas de Engenharia de Software:

* **Entrevistas:** Realizadas com médicos, enfermeiros, administradores e pacientes, com o objetivo de compreender as necessidades de cada perfil.
* **Observação:** Análise de como ocorre atualmente o processo de agendamento das consultas.
* **Brainstorming:** Reuniões entre a equipe técnica e especialistas do hospital para identificar oportunidades de melhoria.
* **Análise Documental:** Consulta às normas internas do hospital e aos requisitos legais referentes à proteção de dados dos pacientes.
* **Protótipos:** Construção de protótipos das APIs e dos fluxos de atendimento para validação dos requisitos junto aos usuários.

---

## 5. UML - Introdução
A UML (Unified Modeling Language) é uma linguagem padrão para modelagem de sistemas orientados a objetos. Seu objetivo é representar visualmente os elementos do software antes da implementação.

Para este projeto, os principais diagramas que poderiam ser utilizados são:
- Casos de Uso;
- Classes;
- Sequência;
- Componentes;
- Implantação.

Neste documento é detalhado o Diagrama de Casos de Uso.

---

## 6. Diagrama de Casos de Uso

**Atores:**
- Médico
- Enfermeiro
- Paciente
- Sistema de Notificações

**Casos de Uso:**
- Autenticar usuário
- Registrar consulta
- Editar consulta
- Consultar histórico
- Consultar próximas consultas
- Visualizar consultas
- Enviar lembrete

### Relação Ator × Caso de Uso

| Ator | Caso de Uso |
| :--- | :--- |
| **Médico** | Registrar Consulta |
| **Médico** | Editar Consulta |
| **Médico** | Consultar Histórico |
| **Enfermeiro** | Registrar Consulta |
| **Enfermeiro** | Consultar Histórico |
| **Paciente** | Visualizar Consultas |
| **Paciente** | Consultar Próximas Consultas |
| **Sistema de Notificações** | Enviar Lembretes |

---

## 7. Documentação dos Casos de Uso

### UC01 - Autenticar Usuário
- **Objetivo:** Permitir o acesso seguro ao sistema.
- **Atores:** Médico, Enfermeiro e Paciente.
- **Pré-condição:** Usuário previamente cadastrado.
- **Fluxo Principal:**
  1. Informar usuário.
  2. Informar senha.
  3. Validar credenciais.
  4. Liberar acesso.
- **Pós-condição:** Usuário autenticado.

### UC02 - Registrar Consulta
- **Ator:** Médico ou Enfermeiro.
- **Fluxo:**
  1. Selecionar paciente.
  2. Informar data.
  3. Informar horário.
  4. Salvar consulta.
  5. Publicar evento para RabbitMQ/Kafka.
- **Pós-condição:** Consulta cadastrada.

### UC03 - Editar Consulta
- **Ator:** Médico.
- **Fluxo:**
  1. Localizar consulta.
  2. Alterar dados.
  3. Salvar alterações.
  4. Publicar evento para notificações.

### UC04 - Consultar Histórico
- **Atores:** Médico e Enfermeiro.
- **Fluxo:**
  1. Selecionar paciente.
  2. Sistema consulta o histórico via GraphQL.
  3. Histórico é retornado.

### UC05 - Visualizar Consultas
- **Ator:** Paciente.
- **Fluxo:**
  1. Login.
  2. Consultar agenda.
  3. Sistema retorna apenas as consultas do paciente.

### UC06 - Enviar Lembrete
- **Ator:** Sistema de Notificações.
- **Fluxo:**
  1. Receber mensagem.
  2. Processar evento.
  3. Identificar paciente.
  4. Enviar lembrete automaticamente.

---

## 8. Considerações Finais
O sistema hospitalar proposto atende aos requisitos apresentados no Tech Challenge ao utilizar autenticação com Spring Security, controle de acesso baseado em perfis, consultas por GraphQL, comunicação assíncrona por RabbitMQ ou Kafka e arquitetura modular baseada em microsserviços.

A documentação desenvolvida aplica práticas de Engenharia de Software para organizar os requisitos, descrever os processos de desenvolvimento e especificar os principais casos de uso, facilitando a implementação, a manutenção e a evolução do sistema.

As informações específicas do desafio foram extraídas do material fornecido, enquanto a estrutura de engenharia foi elaborada para atender aos padrões acadêmicos de documentação.

---
*Documento elaborado para o Tech Challenge - Fase 3 - Engenharia de Software*
Documento_Engenharia_Software_TechChallenge_Fase3.md
Exibindo Documento_Engenharia_Software_TechChallenge_Fase3.md.