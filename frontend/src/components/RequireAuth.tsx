import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { getUsuarioLogado } from '../api/auth'

export function RequireAuth() {
  const location = useLocation()
  const usuario = getUsuarioLogado()

  if (!usuario) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }

  return <Outlet />
}
