import { getToken } from './auth'
import type {
  CadastroLotePayload,
  DescarteApi,
  LoginResponse,
  LoteApi,
  LoteMatrizApi,
  MeResponse,
  MedicamentoApi,
  NovaTransferenciaPayload,
  TransferenciaApi,
  UnidadeApi,
  Usuario,
} from './types'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''

export class ApiError extends Error {
  readonly status: number

  constructor(message: string, status: number) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

async function parseJson<T>(response: Response): Promise<T> {
  const text = await response.text()
  if (!text) {
    return {} as T
  }
  return JSON.parse(text) as T
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  if (options.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }
  const token = getToken()
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  })

  if (!response.ok) {
    let message = `Erro na requisição (${response.status})`
    try {
      const body = await parseJson<{ mensagem?: string; erro?: string }>(response)
      message = body.mensagem ?? body.erro ?? message
    } catch {
      /* corpo vazio ou não-JSON */
    }
    throw new ApiError(message, response.status)
  }

  return parseJson<T>(response)
}

export async function login(
  email: string,
  senha: string
): Promise<{ usuario: Usuario; token: string }> {
  const data = await request<LoginResponse>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, senha }),
  })

  if (!data.sucesso || !('usuario' in data) || !data.token) {
    throw new ApiError(
      'mensagem' in data && data.mensagem ? data.mensagem : 'Credenciais inválidas',
      401
    )
  }

  return { usuario: data.usuario, token: data.token }
}

export function obterUsuarioAutenticado(): Promise<Usuario> {
  return request<MeResponse>('/api/auth/me').then((data) => data.usuario)
}

export function encerrarSessaoNoServidor(): Promise<{ sucesso: boolean }> {
  return request('/api/auth/logout', { method: 'POST' })
}

export function listarLotes(): Promise<LoteApi[]> {
  return request<LoteApi[]>('/api/lotes')
}

export function obterLote(id: number): Promise<LoteApi> {
  return request<LoteApi>(`/api/lotes/${id}`)
}

export function cadastrarLote(payload: CadastroLotePayload): Promise<{ sucesso: boolean; id: number }> {
  return request('/api/lotes', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function listarMatrizSalvacao(): Promise<LoteMatrizApi[]> {
  return request<LoteMatrizApi[]>('/api/matriz-salvacao')
}

export function listarTransferencias(): Promise<TransferenciaApi[]> {
  return request<TransferenciaApi[]>('/api/transferencias')
}

export type StatusTransferenciaApi =
  | 'APROVADA'
  | 'REJEITADA'
  | 'CANCELADA'

export function atualizarStatusTransferencia(
  id: number,
  status: StatusTransferenciaApi
): Promise<{ sucesso: boolean }> {
  return request(`/api/transferencias/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status }),
  })
}

export function criarTransferencia(
  payload: NovaTransferenciaPayload
): Promise<{ sucesso: boolean }> {
  return request('/api/transferencias', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function listarDescartes(): Promise<DescarteApi[]> {
  return request<DescarteApi[]>('/api/descartes')
}

export function listarUnidades(): Promise<UnidadeApi[]> {
  return request<UnidadeApi[]>('/api/unidades')
}

export function listarMedicamentos(): Promise<MedicamentoApi[]> {
  return request<MedicamentoApi[]>('/api/medicamentos')
}
