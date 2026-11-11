
import { useState } from 'react'
import { NavLink } from 'react-router-dom'
import packageIcon from '../assets/icons/package.svg'
import transferIcon from '../assets/icons/transfer.svg'
import healthIcon from '../assets/icons/health.svg'
import menuIcon from '../assets/icons/menu.svg'
import alertIcon from '../assets/icons/alert.svg'

const menuItems = [
  { name: 'Estoque e Lotes', path: '/estoque', icon: packageIcon },
  { name: 'Transferências', path: '/transferencias', icon: transferIcon },
  { name: 'Matriz de Salvação', path: '/matriz-salvacao', icon: healthIcon },
  { name: 'Descartes Ecológicos', path: '/descartes', icon: alertIcon },
]

export function Sidebar() {
  const [collapsed, setCollapsed] = useState(
    () => localStorage.getItem('sustentamed-sidebar-collapsed') === 'true'
  )

  function toggleCollapsed() {
    const nextCollapsed = !collapsed
    localStorage.setItem('sustentamed-sidebar-collapsed', String(nextCollapsed))
    setCollapsed(nextCollapsed)
  }

  return (
    <aside
      className={`box-border flex w-full shrink-0 flex-col border-b border-[#29342d] bg-[#0e1712] p-5 transition-[width] duration-200 md:min-h-screen md:border-b-0 md:border-r ${
        collapsed ? 'md:w-20' : 'md:w-64'
      }`}
    >
      <div
        className={`mb-10 flex items-center ${
          collapsed ? 'justify-center' : 'justify-between gap-3'
        }`}
      >
        <button
          type="button"
          onClick={toggleCollapsed}
          aria-label={collapsed ? 'Expandir barra lateral' : 'Recolher barra lateral'}
          aria-expanded={!collapsed}
          title={collapsed ? 'Expandir barra lateral' : 'Recolher barra lateral'}
          className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-[#205b3e] transition hover:bg-[#28744c] focus-visible:outline focus-visible:outline-2 focus-visible:outline-[#72e0a0]"
        >
          <img
            src={menuIcon}
            alt=""
            aria-hidden="true"
            className="ui-icon h-5 w-5"
          />
        </button>

        {!collapsed && (
          <div className="flex min-w-0 flex-1 flex-col">
            <span className="m-0 whitespace-nowrap text-lg font-bold leading-tight tracking-tight text-white">
              SustentaMed
            </span>
            <span className="whitespace-nowrap text-[10px] leading-tight text-[#829187]">
              Gestão inteligente de saúde
            </span>
          </div>
        )}
      </div>

      <nav className="flex flex-col gap-2">
        {menuItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            aria-label={item.name}
            title={collapsed ? item.name : undefined}
            className={({ isActive }) =>
              `flex items-center rounded-lg py-3 text-sm transition ${
                collapsed ? 'justify-center px-0' : 'gap-3 px-4'
              } ${
                isActive
                  ? 'bg-[#205b3e] text-white'
                  : 'text-[#a6b3aa] hover:bg-[#1a2920] hover:text-white'
              }`
            }
          >
            <img
              src={item.icon}
              alt=""
              aria-hidden="true"
              className="ui-icon h-5 w-5 shrink-0"
            />
            {!collapsed && <span>{item.name}</span>}
          </NavLink>
        ))}
      </nav>

      {!collapsed && (
        <div className="mt-auto hidden border-t border-[#29342d] pt-5 md:block">
        <p className="text-xs text-[#829187]">SustentaMed · Sistema de Gestão</p>
        </div>
      )}
    </aside>
  )
}
