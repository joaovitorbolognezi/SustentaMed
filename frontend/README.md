# SustentaMed — Frontend

Interface React (Vite + TypeScript + Tailwind) para gestão de estoque, matriz de salvação e transferências entre UBS.

## Pré-requisitos

- Node.js 18+
- API Java em `http://localhost:8080` (`com.sustentamed.app.ServidorHttp`)
- Banco MySQL configurado conforme o README na raiz do projeto

## Desenvolvimento

```bash
npm install
npm run dev
```

O proxy em `vite.config.ts` encaminha `/api` para o backend local. Não é necessário configurar CORS manualmente no navegador durante o dev.

## Build

```bash
npm run build
npm run preview
```

Para preview ou deploy sem proxy, defina `VITE_API_BASE_URL=http://localhost:8080` (ou a URL do servidor em produção).

## Estrutura relevante

| Caminho | Função |
|---------|--------|
| `src/api/client.ts` | Cliente HTTP da API |
| `src/features/auth/login.tsx` | Autenticação |
| `src/features/stock/Estoque.tsx` | Lotes |
| `src/features/salvation/MatrizSalvacao.tsx` | Priorização por validade |
| `src/features/transfers/Transferencias.tsx` | Solicitações, criação e status |
| `src/features/descartes/Descartes.tsx` | Histórico de descartes ecológicos |

Diretrizes visuais: [FRONTEND_GUIDELINES.md](./FRONTEND_GUIDELINES.md).

## Credenciais de teste

Use os usuários do `database/seed.sql` (ex.: `camila.farmacia@ubs.gov.br` / `123456`).
