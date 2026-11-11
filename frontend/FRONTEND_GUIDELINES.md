# Diretrizes de desenvolvimento frontend

## Tipografia

- Use **Nunito** como fonte principal para textos, formulários, navegação e botões.
- Use **Montserrat** para títulos e cabeçalhos.
- As duas fontes são carregadas globalmente em `index.html` e configuradas em `src/index.css`. Mantenha essas variáveis como ponto único de configuração para todas as telas, incluindo login.
- Preserve fontes de fallback para que a interface continue legível quando as fontes externas não estiverem disponíveis.

## Ícones

- Prefira arquivos `.svg` separados em `src/assets/icons/` e importe cada ícone diretamente no componente que o utiliza. Exiba o asset com uma tag `<img>` para manter o SVG original fora do JSX e evitar sequências longas de coordenadas `path`.
- `src/components/SvgRepoIcon.tsx` permanece como um pequeno componente reutilizável para os ícones de sol e lua do controle de tema; não use esse componente como catálogo central de todos os ícones das telas.
- Os ícones são assets do SVG Repo com licença CC0. `src/index.css` aplica o tratamento visual adequado aos temas claro e escuro.
- Atributos dos ícones precisam ser acessíveis: ícones decorativos devem permanecer ocultos para leitores de tela; botões contendo apenas ícones precisam de um nome acessível.
- Fontes utilizadas (licença CC0):
  - [Package](https://www.svgrepo.com/svg/506267/package)
  - [Transfer Horizontal](https://www.svgrepo.com/svg/530013/transfer-horizontal)
  - [Heart Pulse](https://www.svgrepo.com/svg/529638/heart-pulse)
  - [Alert Triangle](https://www.svgrepo.com/svg/507904/alert-triangle)
  - [Check Circle](https://www.svgrepo.com/svg/528887/check-circle)
  - [Clock](https://www.svgrepo.com/svg/535304/clock)
  - [Sun](https://www.svgrepo.com/svg/535669/sun)
  - [Crescent Moon](https://www.svgrepo.com/svg/403150/crescent-moon)
  - [Close Circle](https://www.svgrepo.com/svg/528912/close-circle)
  - [Arrow Right](https://www.svgrepo.com/svg/535153/arrow-right)

## Integração com a API

- Chamadas HTTP ficam em `src/api/client.ts`; tipos em `src/api/types.ts`.
- Em desenvolvimento, use rotas relativas (`/api/...`) — o Vite faz proxy para `localhost:8080`.
- A sessão do login é guardada em `sessionStorage` (`src/api/auth.ts`); telas autenticadas usam `RequireAuth`.
- Exiba erros de rede ou validação com mensagem clara; estados de carregamento em listas e formulários.

## Cores e temas

- O modo escuro é o padrão; o controle de tema deve continuar disponível no cabeçalho das telas de gestão e manter a preferência do usuário.
- A barra lateral deve oferecer um botão de recolher/expandir. Recolhida, mostra os ícones e mantém rótulos acessíveis; expandida, mostra ícones e textos. Preserve a preferência do usuário entre visitas.
- Garanta contraste suficiente entre texto, fundo, cartões e estados de prioridade nos dois temas.
- No tema claro, os cartões de prioridade vermelha, laranja, amarela e verde devem ter fundos e bordas distinguíveis do fundo branco, sem prejudicar a legibilidade do texto.
- Use cores semânticas de estado com texto legível; não dependa apenas da cor para comunicar a prioridade.
