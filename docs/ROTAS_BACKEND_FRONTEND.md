# Contrato de rotas — backend e frontend

## Situação atual

O frontend React consome a API REST de `ServidorHttp.java` (porta **8080**). Em desenvolvimento, o Vite faz proxy de `/api/*` para o backend.

Autenticação por token Bearer retornado no login (sessões em memória no servidor Java). O frontend envia `Authorization: Bearer <token>` nas requisições autenticadas.

## Mapeamento tela ↔ rota

| Tela | Método | Rota | Integrado |
|------|--------|------|-----------|
| Login | `POST` | `/api/auth/login` | Sim |
| Sessão | `GET` | `/api/auth/me` | Sim (validação ao abrir o layout) |
| Sessão | `POST` | `/api/auth/logout` | Sim (botão sair) |
| Estoque | `GET` | `/api/lotes` | Sim |
| Estoque | `GET` | `/api/lotes/{id}` | Sim (modal ao clicar na linha) |
| Estoque | `POST` | `/api/lotes` | Sim |
| Estoque / Transferências | `GET` | `/api/medicamentos` | Sim |
| Estoque / Transferências | `GET` | `/api/unidades` | Sim |
| Matriz de Salvação | `GET` | `/api/matriz-salvacao` | Sim |
| Transferências | `GET` | `/api/transferencias` | Sim |
| Transferências | `POST` | `/api/transferencias` | Sim (modal “Nova solicitação”) |
| Transferências | `PATCH` | `/api/transferencias/{id}/status` | Sim |
| Descartes | `GET` | `/api/descartes` | Sim |

## Rotas do backend ainda sem fluxo completo na UI

| Método | Rota | Observação |
|--------|------|------------|
| `POST` | `/api/descartes` | Serviço Java existe; a tela atual só lista (`GET`). Incluir formulário se for necessário solicitar descarte pelo frontend. |

## Payloads principais

### `POST /api/auth/login`

Resposta `200`:

```json
{
  "sucesso": true,
  "token": "uuid-da-sessao",
  "usuario": {
    "id": 1,
    "nome": "Dra. Camila Rocha",
    "email": "camila.farmacia@ubs.gov.br",
    "tipo": "FARMACEUTICA",
    "unidade": { "id": 1, "nome": "UBS Central" }
  }
}
```

### `GET /api/auth/me`

Requer header `Authorization: Bearer <token>`.

### `POST /api/transferencias`

```json
{
  "loteId": 2,
  "origemId": 1,
  "destinoId": 2,
  "quantidade": 50,
  "observacao": "Reposição entre UBS"
}
```

### `GET /api/descartes`

Lista enriquecida com `medicamento`, `numeroLote`, `unidade`, `empresa`, `motivo`, `status`, `data`.

### `GET /api/lotes/{id}`

Mesmo formato do item da listagem, incluindo `diasRestantes`, `prioridade`, `unidadeId` e `medicamentoId`.

## Como executar

1. MySQL + `schema.sql` + `seed.sql`
2. Servidor HTTP Java na porta 8080
3. `cd frontend && npm run dev`

Credenciais de teste: `camila.farmacia@ubs.gov.br` / `123456`.
