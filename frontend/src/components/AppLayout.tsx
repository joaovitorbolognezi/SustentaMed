
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { Sidebar } from './Sidebar'
import { SvgRepoIcon } from './SvgRepoIcon'
import {
  getUsuarioLogado,
  limparSessao,
  setUsuarioLogado,
} from '../api/auth'
import { ApiError, encerrarSessaoNoServidor, obterUsuarioAutenticado } from '../api/client'

const pageTitles: Record<string, string> = {
  '/estoque': 'Estoque e Lotes',
  '/transferencias': 'Transferências',
  '/matriz-salvacao': 'Matriz de Salvação',
  '/descartes': 'Descartes Ecológicos',
}

type Theme = 'dark' | 'light'

export function AppLayout() {
  const location = useLocation()
  const navigate = useNavigate()
  const [usuario, setUsuario] = useState(() => getUsuarioLogado())
  const [theme, setTheme] = useState<Theme>(() =>
    localStorage.getItem('sustentamed-theme') === 'light' ? 'light' : 'dark'
  )

  useEffect(() => {
    obterUsuarioAutenticado()
      .then((dados) => {
        setUsuario(dados)
        setUsuarioLogado(dados)
      })
      .catch((err: unknown) => {
        if (err instanceof ApiError && err.status === 401) {
          limparSessao()
          navigate('/login', { replace: true })
        }
      })
  }, [navigate])

  const title = pageTitles[location.pathname] || 'SustentaMed'
  const iniciais = usuario?.nome
    ? usuario.nome
        .split(' ')
        .filter(Boolean)
        .slice(0, 2)
        .map((parte) => parte[0]?.toUpperCase())
        .join('')
    : 'SM'

  async function sair() {
    try {
      await encerrarSessaoNoServidor()
    } catch {
      /* encerra localmente mesmo se a API estiver indisponível */
    }
    limparSessao()
    navigate('/login', { replace: true })
  }

  function toggleTheme() {
    const nextTheme = theme === 'dark' ? 'light' : 'dark'
    localStorage.setItem('sustentamed-theme', nextTheme)
    setTheme(nextTheme)
  }

  return (
    <div
      data-theme={theme}
      className="flex min-h-screen flex-col bg-[#0b100e] text-[#f2f7f3] md:flex-row"
    >
      <Sidebar />

      <main className="min-w-0 flex-1">
        <header className="flex min-h-20 flex-wrap items-center justify-between border-b border-[#29342d] px-6 py-4 md:flex-nowrap md:px-10">
          <div>
            <p className="mb-1 text-[10px] font-bold tracking-[0.18em] text-[#72e0a0]">
              SUSTENTAMED / GESTÃO
            </p>

            <h2 className="text-xl font-semibold text-white">
              {title}
            </h2>
            {usuario?.unidade ? (
              <p className="mt-1 text-xs text-[#829187]">{usuario.unidade.nome}</p>
            ) : null}
          </div>

          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={toggleTheme}
              className="theme-toggle"
              aria-pressed={theme === 'light'}
            >
              <SvgRepoIcon
                name={theme === 'dark' ? 'sun' : 'moon'}
                className="h-5 w-5"
              />
              <span>
                Alterar para modo {theme === 'dark' ? 'claro' : 'escuro'}
              </span>
            </button>

            <button
              type="button"
              onClick={() => void sair()}
              title={usuario ? `${usuario.nome} — sair` : 'Sair'}
              className="flex h-10 min-w-10 items-center justify-center rounded-full border border-[#3a5945] bg-[#205b3e]/40 px-2 font-bold text-[#a0f2bb]"
            >
              {iniciais}
            </button>
          </div>
        </header>

        <div className="p-5 md:p-10">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
