
import { Outlet, useLocation } from 'react-router-dom'
import { useState } from 'react'
import { Sidebar } from './Sidebar'
import { SvgRepoIcon } from './SvgRepoIcon'

const pageTitles: Record<string, string> = {
  '/estoque': 'Estoque e Lotes',
  '/transferencias': 'Transferências',
  '/matriz-salvacao': 'Matriz de Salvação',
}

type Theme = 'dark' | 'light'

export function AppLayout() {
  const location = useLocation()
  const [theme, setTheme] = useState<Theme>(() =>
    localStorage.getItem('sustentamed-theme') === 'light' ? 'light' : 'dark'
  )

  const title = pageTitles[location.pathname] || 'SustentaMed'

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

            <div className="flex h-10 w-10 items-center justify-center rounded-full border border-[#3a5945] bg-[#205b3e]/40 font-bold text-[#a0f2bb]">
              SM
            </div>
          </div>
        </header>

        <div className="p-5 md:p-10">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
