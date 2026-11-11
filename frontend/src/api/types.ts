export type UnidadeResumo = {
  id: number
  nome: string
}

export type Usuario = {
  id: number
  nome: string
  email: string
  tipo: string
  unidade?: UnidadeResumo
}

export type LoginResponse =
  | { sucesso: true; token: string; usuario: Usuario }
  | { sucesso: false; mensagem?: string; erro?: string }

export type MeResponse = { usuario: Usuario }

export type LoteApi = {
  id: number
  medicamento: string
  numeroLote: string
  quantidade: number
  validade: string
  status?: string
  unidade?: string
  unidadeId?: number
  medicamentoId?: number
  diasRestantes?: number
  prioridade?: string
}

export type LoteMatrizApi = LoteApi & {
  diasRestantes: number
  prioridade: string
}

export type TransferenciaApi = {
  id: number
  medicamento: string
  origem: string
  destino: string
  quantidade: number
  status: string
  data: string
}

export type DescarteApi = {
  id: number
  motivo: string
  status: string
  data: string
  medicamento: string
  numeroLote: string
  unidade: string
  empresa: string
}

export type UnidadeApi = {
  id: number
  cnes: string
  nome: string
}

export type MedicamentoApi = {
  id: number
  nome: string
}

export type CadastroLotePayload = {
  medicamentoId: number
  numeroLote: string
  quantidade: number
  validade: string
  unidadeId: number
}

export type NovaTransferenciaPayload = {
  loteId: number
  origemId: number
  destinoId: number
  quantidade: number
  observacao?: string
}
