export function situacaoPorValidade(validade: string): string {
  const hoje = new Date()
  hoje.setHours(0, 0, 0, 0)

  const dataValidade = new Date(`${validade}T00:00:00`)
  const diasRestantes = Math.ceil(
    (dataValidade.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24)
  )

  if (diasRestantes < 0) return 'Vencido'
  if (diasRestantes <= 30) return 'Próximo do Vencimento'
  return 'Disponível'
}

export function prioridadePorValidade(validade: string): string {
  const hoje = new Date()
  hoje.setHours(0, 0, 0, 0)
  const dataValidade = new Date(`${validade}T00:00:00`)
  const dias = Math.ceil(
    (dataValidade.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24)
  )

  if (dias < 0) return 'Vencido'
  if (dias <= 7) return 'Crítico'
  if (dias <= 30) return 'Atenção'
  return 'Normal'
}

export function diasAteValidade(validade: string): number {
  const hoje = new Date()
  hoje.setHours(0, 0, 0, 0)
  const dataValidade = new Date(`${validade}T00:00:00`)
  return Math.ceil(
    (dataValidade.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24)
  )
}

export function formatarStatusDescarte(status: string): string {
  const mapa: Record<string, string> = {
    SOLICITADO: 'Solicitado',
    COLETADO: 'Coletado',
    CONCLUIDO: 'Concluído',
    CANCELADO: 'Cancelado',
  }
  return mapa[status] ?? status
}

export function formatarStatusTransferencia(status: string): string {
  const mapa: Record<string, string> = {
    PENDENTE: 'Pendente',
    APROVADA: 'Aprovada',
    REJEITADA: 'Rejeitada',
    CANCELADA: 'Cancelada',
  }
  return mapa[status] ?? status
}

export function formatarDataBr(isoOuLocal: string): string {
  const soData = isoOuLocal.includes('T') ? isoOuLocal.split('T')[0] : isoOuLocal
  if (/^\d{4}-\d{2}-\d{2}$/.test(soData)) {
    return new Date(`${soData}T00:00:00`).toLocaleDateString('pt-BR')
  }
  return new Date(isoOuLocal).toLocaleDateString('pt-BR')
}
