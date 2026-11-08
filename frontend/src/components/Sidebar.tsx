
import { NavLink } from 'react-router-dom'

const menuItems = [
  { name: 'Estoque e Lotes', path: '/estoque', icon: '▦' },
  { name: 'Transferências', path: '/transferencias', icon: '⇄' },
  { name: 'Matriz de Salvação', path: '/matriz-salvacao', icon: '⌁' },
]

export function Sidebar() {
  return (
    <aside className="flex w-full shrink-0 flex-col border-b border-[#29342d] bg-[#0e1712] p-5 md:min-h-screen md:w-64 md:border-b-0 md:border-r">

      <div className="mb-10 flex items-center gap-3 px-2">
        <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-[#205b3e] text-xl font-bold text-[#a0f2bb]">
          S
        </div>

        <div className="flex min-w-0 flex-col">
          <span className="m-0 whitespace-nowrap text-lg font-bold leading-tight tracking-tight text-white">
            SustentaMed
          </span>
          <span className="whitespace-nowrap text-[10px] leading-tight text-[#829187]">
            Gestão inteligente de saúde
          </span>
        </div>
      </div>

      <nav className="flex flex-col gap-2">
        {menuItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `flex items-center gap-3 rounded-lg px-4 py-3 text-sm transition ${
                isActive
                  ? 'bg-[#205b3e] text-white'
                  : 'text-[#a6b3aa] hover:bg-[#1a2920] hover:text-white'
              }`
            }
          >
            <span className="text-lg">{item.icon}</span>
            <span>{item.name}</span>
          </NavLink>
        ))}
      </nav>

      <div className="mt-auto hidden border-t border-[#29342d] pt-5 md:block">
        <p className="text-xs text-[#829187]">SustentaMed · Sistema de Gestão</p>
      </div>

    </aside>
  )
}
