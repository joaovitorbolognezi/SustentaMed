# 💊 SustentaMed - Backend (Java Puro + JDBC + MySQL)

> **Projeto Acadêmico:** AEP - Atividade Estruturada de Prática (4º Semestre - 2026.2)  
> **Curso:** Bacharelado em Engenharia de Software — UniCesumar  
> **ODS Alinhado:** [ODS 3 – Saúde e Bem-Estar](https://brasil.un.org/pt-br/sdgs/3) (Metas 3.8 e 3.b)  
> **Tecnologia:** Java SE (Standard Edition) Puro + JDBC + MySQL Local  
> **Integrantes:** João Vitor Bolognezi Portela, Lucas França Peloi, Murilo Bianco Montoia  

---

## 📋 Sumário
1. [Visão Geral e Problema Resolvido](#-visão-geral-e-problema-resolvido)
2. [Modelagem e Diagramas](#-modelagem-e-diagramas)
3. [Estrutura do Projeto](#-estrutura-do-projeto)
4. [Pré-requisitos](#-pré-requisitos)
5. [Passo a Passo: Como Rodar do GitHub](#-passo-a-passo-como-rodar-do-github)
   - [1. Clonar o Repositório](#1-clonar-o-repositório)
   - [2. Configurar o Banco de Dados MySQL](#2-configurar-o-banco-de-dados-mysql)
   - [3. Configurar a Conexão no Java](#3-configurar-a-conexão-no-java)
   - [4. Compilar o Projeto](#4-compilar-o-projeto)
   - [5. Executar a Aplicação](#5-executar-a-aplicação)
   - [6. Frontend Web (opcional)](#6-frontend-web-opcional)
6. [Executando pela IDE (IntelliJ IDEA / VS Code / Eclipse)](#-executando-pela-ide)
7. [Endpoints da API REST Nativa](#-endpoints-da-api-rest-nativa)
8. [Credenciais de Teste (Seed)](#-credenciais-de-teste-seed)

---

## 🎯 Visão Geral e Problema Resolvido

O **SustentaMed** é uma solução para a gestão sustentável do ciclo de medicamentos em Unidades Básicas de Saúde (UBSs), combatendo dois problemas simultâneos: o desabastecimento em áreas periféricas e o desperdício por descarte de fármacos vencidos sem distribuição oportuna.

### Funcionalidades Centrais:
* **Controle de Estoque e Lotes**: Rastreabilidade de lotes com data de fabricação, validade e quantitativos.
* **Matriz de Salvação (Prevenção Proativa de Perdas)**: Algoritmo que calcula os dias restantes para o vencimento de cada lote e categoriza a urgência:
  - 🚨 **Crítico** ($\le 7$ dias restantes): Urgência imediata de redistribuição comunitária.
  - ⚠️ **Atenção** ($\le 30$ dias restantes): Prioridade alta de consumo/transferência.
  - ❌ **Vencido** ($< 0$ dias): Bloqueado para dispensação e marcado para descarte ecológico.
* **Redistribuição Comunitária**: Solicitação de transferências de medicamentos entre diferentes UBSs com controle de status (`PENDENTE`, `APROVADA`, `REJEITADA`) e baixa automática de estoque.
* **Descarte Ecológico**: Encaminhamento seguro de medicamentos vencidos para empresas credenciadas de incineração ambiental.

---

## 📐 Modelagem e Diagramas

O backend foi implementado respeitando rigorosamente os diagramas da pasta `docs/`:

* **Diagrama de Classes UML:** [docs/diagrama_classes.png](docs/diagrama_classes.png)  
  * Herança de Usuários: `Usuario` ➔ `Farmaceutica` (CRF) e `Enfermeira` (COREN).  
  * Herança de Insumos: `InsumoMedico` ➔ `Medicamento`.  
  * Entidades associativas: `LoteMedicamento`, `SolicitacaoTransferencia`, `Descarte`, `EmpresaDescarte` e `UnidadeSaude`.
* **Diagrama Entidade-Relacionamento (DER):** [docs/diagrama_der.png](docs/diagrama_der.png)  
  * 7 tabelas normalizadas com chaves primárias e relacionamentos com integridade referencial.
* **Contrato REST (frontend):** [docs/ROTAS_BACKEND_FRONTEND.md](docs/ROTAS_BACKEND_FRONTEND.md)

---

## 📂 Estrutura do Projeto

```text
SustentaMed/ (raiz do repositório AEP)
├── database/
│   ├── schema.sql                     # Script DDL de criação do banco e tabelas
│   └── seed.sql                       # Carga de dados iniciais (UBSs, usuários, medicamentos, lotes)
├── docs/
│   ├── diagrama_classes.png           # Diagrama de Classes UML
│   ├── diagrama_der.png               # Diagrama de Banco de Dados DER
│   └── ROTAS_BACKEND_FRONTEND.md      # Mapeamento API ↔ telas
├── lib/
│   └── mysql-connector-j.jar          # Driver oficial JDBC do MySQL
├── bin/                               # Bytecodes compilados (.class)
├── src/com/sustentamed/
│   ├── factory/
│   │   └── ConnectionFactory.java     # Gerenciamento da conexão com o MySQL
│   ├── model/                         # Entidades e regras de negócio de domínio
│   │   ├── enums/                     # StatusLote, StatusTransferencia, StatusDescarte, etc.
│   │   ├── UnidadeSaude.java
│   │   ├── Usuario.java
│   │   ├── Farmaceutica.java
│   │   ├── Enfermeira.java
│   │   ├── InsumoMedico.java
│   │   ├── Medicamento.java
│   │   ├── LoteMedicamento.java       # Lógica da Matriz de Salvação e cálculo de dias
│   │   ├── SolicitacaoTransferencia.java
│   │   ├── EmpresaDescarte.java
│   │   └── Descarte.java
│   ├── dao/                           # Padrão Data Access Object (PreparedStatement/ResultSet)
│   │   ├── UnidadeSaudeDAO.java
│   │   ├── UsuarioDAO.java
│   │   ├── MedicamentoDAO.java
│   │   ├── LoteMedicamentoDAO.java
│   │   ├── SolicitacaoTransferenciaDAO.java
│   │   ├── EmpresaDescarteDAO.java
│   │   └── DescarteDAO.java
│   ├── service/                       # Regras de Negócio e Validações
│   │   ├── AuthService.java
│   │   ├── EstoqueService.java
│   │   ├── TransferenciaService.java
│   │   └── DescarteService.java
│   └── app/
│       ├── Main.java                  # Aplicação Console Interativa (Apresentação AEP)
│       ├── ServidorHttp.java          # API REST Nativa (Porta 8080) com CORS para o Frontend
│       └── TestaConexao.java          # Diagnóstico rápido do banco de dados
├── frontend/                          # React + Vite + TypeScript + Tailwind
│   ├── package.json
│   ├── vite.config.ts
│   ├── index.html
│   ├── FRONTEND_GUIDELINES.md
│   ├── README.md
│   └── src/
│       ├── api/
│       │   ├── client.ts              # Cliente HTTP da API REST
│       │   ├── auth.ts                # Sessão (token/usuário no navegador)
│       │   ├── types.ts
│       │   └── loteUtils.ts
│       ├── components/
│       │   ├── AppLayout.tsx
│       │   ├── Sidebar.tsx
│       │   ├── RequireAuth.tsx
│       │   └── SvgRepoIcon.tsx
│       ├── features/
│       │   ├── auth/login.tsx
│       │   ├── stock/Estoque.tsx
│       │   ├── transfers/Transferencias.tsx
│       │   ├── salvation/MatrizSalvacao.tsx
│       │   └── descartes/Descartes.tsx
│       ├── assets/icons/              # Ícones SVG (UI)
│       ├── App.tsx
│       ├── main.tsx
│       └── index.css
├── executar_menu.bat                  # Executável do Menu no Console (Windows)
├── iniciar_servidor_http.bat          # Executável da API REST (Windows)
├── .gitignore                         # Arquivos ignorados no repositório Git
└── README.md                          # Esta documentação
```

---

## 🛠️ Pré-requisitos

Para executar este projeto em qualquer máquina, você precisará de:
1. **Java JDK 8 ou superior** (Java 17 ou 21 recomendado). Verifique no terminal com:
   ```bash
   java -version
   ```
2. **MySQL Server** (versão 8.0 ou superior) em execução localmente na porta padrão `3306`.
3. **Node.js 18+** (somente para o frontend web).
4. **Git** para clonar o repositório.

---

## 🚀 Passo a Passo: Como Rodar do GitHub

### 1. Clonar o Repositório
Abra o terminal (Git Bash, Prompt de Comando ou PowerShell) e clone o repositório:
```bash
git clone https://github.com/joaovitorbolognezi/SustentaMed.git
cd SustentaMed
```

> A pasta `bin/` **não** vai para o Git. Após clonar, compile o Java (passo 4).

---

### 2. Configurar o Banco de Dados MySQL
No seu gerenciador de banco (MySQL Workbench, DBeaver ou terminal MySQL):
1. Execute o script de criação das tabelas:
   * Abra e execute o arquivo `database/schema.sql`.
   * Ele criará a base `sistema_saude` e todas as 7 tabelas.
2. Execute o script de carga inicial:
   * Abra e execute o arquivo `database/seed.sql`.
   * Ele carregará as UBSs, usuários, catálogo de medicamentos e lotes para teste da Matriz de Salvação.

---

### 3. Configurar a Conexão no Java
Abra o arquivo `src/com/sustentamed/factory/ConnectionFactory.java`:
```java
private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/sistema_saude?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
private static final String DEFAULT_USER = "root";
private static final String DEFAULT_PASSWORD = ""; // <- Se o seu MySQL tiver senha, informe aqui
```
> **Dica:** Você também pode informar a senha sem alterar o código, passando o parâmetro ao rodar:  
> `java -Ddb.password=suasenha -cp "bin;lib/mysql-connector-j.jar" com.sustentamed.app.Main`

---

### 4. Compilar o Projeto

Gere os `.class` na pasta `bin/` (obrigatório na primeira vez e após alterar o Java):

**Windows (PowerShell), na raiz do projeto:**

```powershell
New-Item -ItemType Directory -Force -Path bin | Out-Null
javac -encoding UTF-8 -d bin -cp "lib/mysql-connector-j.jar" (Get-ChildItem -Recurse -Path src -Filter *.java).FullName
```

**Linux / macOS:**

```bash
mkdir -p bin
find src -name "*.java" -print0 | xargs -0 javac -encoding UTF-8 -d bin -cp "lib/mysql-connector-j.jar"
```

Alternativa: abra o projeto na IDE, marque `src` como fonte, adicione `lib/mysql-connector-j.jar` e use **Run** em `ServidorHttp.java` ou `Main.java` (a IDE compila automaticamente).

---

### 5. Executar a Aplicação

Você tem **duas formas** de executar o sistema:

#### Modo A: Menu Interativo no Console
* **Pelo Windows:** Dê um duplo clique no arquivo `executar_menu.bat`  
* **Ou pelo terminal:**
  * **Windows:**
    ```bash
    java -cp "bin;lib/mysql-connector-j.jar" com.sustentamed.app.Main
    ```
  * **Linux / macOS:**
    ```bash
    java -cp "bin:lib/mysql-connector-j.jar" com.sustentamed.app.Main
    ```

**Recursos disponíveis no Menu Console:**
* Autenticação de Farmacêutica/Enfermeira com validação de senha.
* Consulta geral de estoque com visualização de dias restantes e lotes vencidos.
* **Matriz de Salvação**: Exibição dos lotes ranqueados por urgência com alertas visuais.
* Cadastro de novos lotes vinculados à UBS do usuário logado.
* Solicitação e aprovação de transferências entre UBSs com baixa de estoque.
* Encaminhamento para descarte ecológico e confirmação de coleta ambiental.

---

#### Modo B: Servidor HTTP REST Nativo 
* **Pelo Windows:** Dê um duplo clique no arquivo `iniciar_servidor_http.bat`  
* **Ou pelo terminal:**
  * **Windows:**
    ```bash
    java -cp "bin;lib/mysql-connector-j.jar" com.sustentamed.app.ServidorHttp
    ```
  * **Linux / macOS:**
    ```bash
    java -cp "bin:lib/mysql-connector-j.jar" com.sustentamed.app.ServidorHttp
    ```

O servidor iniciará em `http://localhost:8080` com **suporte nativo a CORS** ativado, pronto para responder às requisições do frontend React!

---

### 6. Frontend Web (opcional)

Com a API já em execução:

```bash
cd frontend
npm install
npm run dev
```

Acesse a URL do Vite (ex.: `http://localhost:5173`). Login de teste na seção [Credenciais de Teste](#-credenciais-de-teste-seed).

Build de produção: `npm run build` (saída em `frontend/dist/`). Deploy estático (ex.: Vercel) exige `VITE_API_BASE_URL` apontando para uma API acessível na internet.

---

## 💻 Executando pela IDE

### IntelliJ IDEA:
1. Abra o IntelliJ e vá em `File` > `Open...` e selecione a **raiz do repositório** (pasta do projeto AEP/SustentaMed).
2. Configure a pasta `src` como Sources Root (clique com botão direito em `src` > `Mark Directory as` > `Sources Root`).
3. Adicione o driver JDBC:
   * Vá em `File` > `Project Structure` > `Modules` > aba `Dependencies`.
   * Clique no botão **`+`** > `JARs or Directories...` > selecione `lib/mysql-connector-j.jar`.
4. Abra `src/com/sustentamed/app/Main.java` e clique no ícone verde de **Run** (Executar).

### Visual Studio Code:
1. Abra a **raiz do repositório** no VS Code/Cursor.
2. Instale a extensão **Extension Pack for Java** (se ainda não tiver).
3. No menu lateral esquerdo, vá na seção **Java Projects** > **Referenced Libraries** > clique no ícone **`+`** e selecione o arquivo `lib/mysql-connector-j.jar`.
4. Abra `Main.java` e clique em **Run**.

---

## 🔗 Endpoints da API REST Nativa

O servidor nativo `ServidorHttp.java` expõe os seguintes endpoints:

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Autentica e retorna `token` Bearer + dados do usuário |
| `GET` | `/api/auth/me` | Retorna o usuário da sessão (header `Authorization`) |
| `POST` | `/api/auth/logout` | Encerra a sessão do token |
| `GET` | `/api/lotes` | Lista todos os lotes com estoque e validade |
| `GET` | `/api/lotes/{id}` | Detalhe de um lote específico |
| `POST` | `/api/lotes` | Cadastra novo lote de medicamento |
| `GET` | `/api/matriz-salvacao` | Retorna lotes com urgência (Crítico, Atenção, Vencido) |
| `GET` | `/api/transferencias` | Lista todas as solicitações de transferência |
| `POST` | `/api/transferencias` | Solicita redistribuição de lote para outra UBS |
| `GET` | `/api/descartes` | Lista histórico de descartes ecológicos |
| `GET` | `/api/unidades` | Lista todas as UBSs cadastradas |
| `GET` | `/api/medicamentos` | Lista medicamentos do catálogo (formulário de lotes) |
| `PATCH` | `/api/transferencias/{id}/status` | Aprova, rejeita ou cancela solicitação pendente |

---

## 🌐 Frontend React (integração com a API)

O frontend em `frontend/` consome os endpoints acima. Em desenvolvimento, use o proxy do Vite (`/api` → `http://localhost:8080`).

```bash
cd frontend
npm install
npm run dev
```

1. Inicie o **Servidor HTTP** (Modo B) antes do frontend.
2. Acesse a URL do Vite (porta padrão `5173`).
3. Faça login com as credenciais do seed (tabela abaixo).

Detalhes do contrato e rotas sem tela: [docs/ROTAS_BACKEND_FRONTEND.md](docs/ROTAS_BACKEND_FRONTEND.md).

---

## 🔑 Credenciais de Teste (Seed)

Para entrar no sistema após rodar o `database/seed.sql`:

| Perfil | Nome | E-mail | Senha | Unidade |
| :--- | :--- | :--- | :--- | :--- |
| **Farmacêutica** | Dra. Camila Rocha (CRF-PR 12345) | `camila.farmacia@ubs.gov.br` | `123456` | UBS Central |
| **Enfermeira** | Enf. Juliana Silveira (COREN-PR 67890) | `juliana.enfermagem@ubs.gov.br` | `123456` | UBS Jardim |

---

## 📜 Licença e Propósito Acadêmico

Desenvolvido para fins exclusivamente didáticos como parte da Atividade Estruturada de Prática (AEP) do curso de Engenharia de Software da **UniCesumar**, integrando conceitos de Programação Orientada a Objetos, Padrão DAO, Persistência Relacional via JDBC e boas práticas de Engenharia de Software voltadas aos Objetivos de Desenvolvimento Sustentável (ODS) da ONU.
