
import { Outlet, useLocation } from 'react-router-dom'
import { Sidebar } from './Sidebar'

const pageTitles: Record<string, string> = {
  '/estoque': 'Estoque e Lotes',
  '/transferencias': 'Transferências',
  '/matriz-salvacao': 'Matriz de Salvação',
}

export function AppLayout() {
  const location = useLocation()

  const title = pageTitles[location.pathname] || 'SustentaMed'

  return (
    <div className="flex min-h-screen flex-col bg-[#0b100e] text-[#f2f7f3] md:flex-row">
      <Sidebar />

      <main className="min-w-0 flex-1">
        <header className="flex min-h-20 items-center justify-between border-b border-[#29342d] px-6 py-4 md:px-10">
          <div>
            <p className="mb-1 text-[10px] font-bold tracking-[0.18em] text-[#72e0a0]">
              SUSTENTAMED / GESTÃO
            </p>

            <h2 className="text-xl font-semibold text-white">
              {title}
            </h2>
          </div>

          <div className="flex h-10 w-10 items-center justify-center rounded-full border border-[#3a5945] bg-[#205b3e]/40 font-bold text-[#a0f2bb]">
            SM
          </div>
        </header>

        <div className="p-5 md:p-10">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
