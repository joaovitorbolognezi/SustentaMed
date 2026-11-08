# Contrato de rotas do backend para o frontend

## Situação atual

Este documento descreve as rotas necessárias para integrar as telas atuais a uma API. As rotas abaixo são uma proposta de contrato: o repositório ainda não contém controladores/rotas de backend implementados (`src/` está vazio), e o formulário de login e as telas de gestão ainda usam dados locais.

A base existente é `database/aep.db`. Ela possui tabelas para `usuarios`, `unidades_saude`, `insumos_medicos`, `lotes_medicamento`, `solicitacoes_transferencia`, `descartes` e `empresas_descarte`, mas está sem registros. O modelo atual não guarda todos os dados exibidos na tela de transferências; veja [Ajustes necessários no banco](#ajustes-necessários-no-banco).

## Convenções da API

- Prefixo: `/api`.
- Corpo e respostas em JSON, com nomes de campos `camelCase`.
- Datas de validade no formato ISO `YYYY-MM-DD`; datas de criação em ISO 8601.
- IDs numéricos correspondem às chaves primárias do banco.
- Rotas privadas exigem sessão autenticada e autorização por unidade/perfil.
- Senhas nunca são devolvidas pela API e devem ser armazenadas com hash seguro.
- Estados de lotes e transferências devem ter valores documentados e ser convertidos para os rótulos em português apresentados na interface.

## Rotas por tela

| Tela | Método | Rota | Uso |
|---|---|---|---|
| Login | `POST` | `/api/auth/login` | Autentica o e-mail e a senha do formulário atual. |
| Sessão | `GET` | `/api/auth/me` | Recupera o usuário autenticado e sua unidade. |
| Sessão | `POST` | `/api/auth/logout` | Encerra a sessão. |
| Estoque | `GET` | `/api/lotes` | Lista e pesquisa lotes; aceita `busca` e `status`. A tela calcula os totais a partir da lista. |
| Estoque | `POST` | `/api/lotes` | Cadastra um lote pelo formulário “Novo lote”. |
| Estoque | `GET` | `/api/lotes/{id}` | Recupera os dados de um lote específico, para detalhes e edição futuros. |
| Matriz de Salvação | `GET` | `/api/matriz-salvacao` | Lista lotes com prioridade calculada, ordenados pela proximidade do vencimento. Aceita `busca` e `prioridade`. |
| Transferências | `GET` | `/api/transferencias` | Lista solicitações; aceita `status`. A tela calcula os contadores a partir da lista. |
| Transferências | `PATCH` | `/api/transferencias/{id}/status` | Aprova, rejeita ou cancela uma solicitação pendente. |
| Unidades | `GET` | `/api/unidades` | Fornece as unidades de saúde e seus nomes para exibir origem/destino e compor formulários. |
| Transferências (criação) | `POST` | `/api/transferencias` | Cria uma solicitação. Preparada para o fluxo de criação; a interface atual ainda não tem esse formulário. |

### `POST /api/auth/login`

Requisição:

```json
{
  "email": "profissional@ubs.gov.br",
  "senha": "senha-informada-no-formulario"
}
```

Resposta `200` (senha omitida):

```json
{
  "usuario": {
    "id": 12,
    "nome": "Ana Souza",
    "email": "profissional@ubs.gov.br",
    "tipoUsuario": "FARMACEUTICO",
    "unidade": {
      "id": 3,
      "nome": "UBS Central"
    }
  }
}
```

Estabelecer a sessão por cookie `HttpOnly`, `Secure` e `SameSite` ou documentar um mecanismo equivalente. Credenciais inválidas devem retornar `401`.

### `GET /api/lotes`

Parâmetros opcionais:

- `busca`: procura pelo nome do medicamento ou número do lote.
- `status`: `DISPONIVEL`, `PROXIMO_VENCIMENTO` ou `VENCIDO`.

Resposta:

```json
[
  {
    "id": 21,
    "medicamento": "Dipirona 500mg",
    "numeroLote": "DIP-2026-001",
    "quantidade": 250,
    "validade": "2027-05-10",
    "status": "DISPONIVEL",
    "unidade": {
      "id": 3,
      "nome": "UBS Central"
    }
  }
]
```

O status de validade pode ser derivado da data atual, usando o mesmo critério do frontend: vencido antes de hoje, próximo do vencimento nos próximos 30 dias, disponível depois disso.

### `POST /api/lotes`

O formulário atual envia o nome do medicamento, número do lote, quantidade e validade. Como a tabela `lotes_medicamento` referencia `insumos_medicos` e `unidades_saude`, o backend deve associar o lote à unidade do usuário autenticado e a um medicamento cadastrado. É preferível que o frontend passe a selecionar `medicamentoId` quando houver catálogo de medicamentos.

Requisição sugerida:

```json
{
  "medicamentoId": 8,
  "numeroLote": "DIP-2026-001",
  "quantidade": 250,
  "validade": "2027-05-10"
}
```

Validar quantidade positiva, validade válida, existência do medicamento e autorização para a unidade. Resposta de criação `201` com o lote no mesmo formato de `GET /api/lotes`.

### `GET /api/matriz-salvacao`

Parâmetros opcionais:

- `busca`: procura por medicamento, lote ou unidade.
- `prioridade`: `VENCIDO`, `CRITICO`, `ATENCAO` ou `NORMAL`.

Resposta:

```json
{
  "resumo": {
    "total": 5,
    "vencidos": 1,
    "criticos": 2,
    "atencao": 1
  },
  "lotes": [
    {
      "id": 21,
      "medicamento": "Amoxicilina 500mg",
      "numeroLote": "AMX-2026-014",
      "unidade": "UBS Central",
      "quantidade": 120,
      "validade": "2026-11-08",
      "diasRestantes": 0,
      "prioridade": "CRITICO"
    }
  ]
}
```

Prioridades usadas pela tela: `VENCIDO` para validade ultrapassada; `CRITICO` para até 7 dias; `ATENCAO` para 8 a 30 dias; `NORMAL` para mais de 30 dias. Como a tela mostra tanto o resumo como a lista, o endpoint pode calcular ambos na mesma consulta.

### `GET /api/transferencias`

Aceita `status` opcional: `PENDENTE`, `APROVADA`, `REJEITADA` ou `CANCELADA`.

Resposta:

```json
[
  {
    "id": 42,
    "medicamento": "Dipirona 500mg",
    "origem": {
      "id": 3,
      "nome": "UBS Central"
    },
    "destino": {
      "id": 4,
      "nome": "UBS Jardim"
    },
    "quantidade": 100,
    "data": "2026-11-04T10:00:00-03:00",
    "status": "PENDENTE"
  }
]
```

### `PATCH /api/transferencias/{id}/status`

Requisição:

```json
{
  "status": "APROVADA"
}
```

Permitir apenas as transições aplicáveis a uma solicitação `PENDENTE`. Na aprovação, validar e atualizar a quantidade do lote dentro de uma transação para impedir estoque negativo ou dupla aprovação. Responder com a solicitação atualizada; usar `404` para ID inexistente e `409` para transição inválida ou conflito de estoque.

### `POST /api/transferencias`

Esta rota completa o fluxo de solicitação, embora a tela atual só liste e altere o estado:

```json
{
  "loteId": 21,
  "unidadeDestinoId": 4,
  "quantidade": 100,
  "observacao": "Reposição solicitada pela unidade"
}
```

A origem deve ser a unidade do usuário autenticado, não um valor confiado do cliente. Iniciar com estado `PENDENTE`.

## Ajustes necessários no banco

A tabela `solicitacoes_transferencia` atual tem `unidade_saude_id`, mas não identifica claramente a origem e o destino, nem o lote/medicamento solicitado. A tela de transferências precisa de todos esses dados. Antes de integrar essa tela:

1. Criar uma migração compatível que acrescente referências explícitas à unidade de origem e destino e ao lote solicitado (por exemplo, `unidade_origem_id`, `unidade_destino_id` e `lote_medicamento_id`).
2. Preservar os registros existentes e mapear `unidade_saude_id` para o significado correto antes de removê-lo ou substituí-lo.
3. Garantir que o fluxo de aprovação atualize o estoque de forma atômica e registre quem aprovou/rejeitou e quando.
4. Adicionar índices e restrições para as chaves estrangeiras e estados válidos.

Os campos de resposta `origem`, `destino` e `medicamento` devem ser montados por junções com `unidades_saude`, `lotes_medicamento` e `insumos_medicos`; não são campos texto armazenados atualmente na tabela de solicitações.

## Respostas de erro

Manter um formato uniforme, por exemplo:

```json
{
  "erro": {
    "codigo": "VALIDACAO",
    "mensagem": "A quantidade deve ser maior que zero."
  }
}
```

Usar códigos HTTP adequados: `400` para dados inválidos, `401` para ausência/invalidade de sessão, `403` para falta de permissão, `404` para recurso inexistente e `409` para conflito de estado ou estoque.

## Fora das rotas atuais

As tabelas de descarte e empresa de descarte ainda não são usadas por nenhuma das rotas/telas implementadas no frontend. Seus endpoints deverão ser especificados quando uma interface de descarte for adicionada.
