import type { Usuario } from './types'

const STORAGE_KEY = 'sustentamed-usuario'
const TOKEN_KEY = 'sustentamed-token'

export function getUsuarioLogado(): Usuario | null {
  const raw = sessionStorage.getItem(STORAGE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as Usuario
  } catch {
    return null
  }
}

export function setUsuarioLogado(usuario: Usuario): void {
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(usuario))
}

export function getToken(): string | null {
  return sessionStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
  sessionStorage.setItem(TOKEN_KEY, token)
}

export function limparSessao(): void {
  sessionStorage.removeItem(STORAGE_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
}
