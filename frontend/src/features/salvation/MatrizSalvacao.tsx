
import { useState } from 'react'

type Lote = {
  id: number
  medicamento: string
  numeroLote: string
  unidade: string
  quantidade: number
  validade: string
}

const dadosIniciais: Lote[] = [
  {
    id: 1,
    medicamento: 'Amoxicilina 500mg',
    numeroLote: 'AMX-2026-014',
    unidade: 'UBS Central',
    quantidade: 120,
    validade: '2026-11-08',
  },
  {
    id: 2,
    medicamento: 'Dipirona 500mg',
    numeroLote: 'DIP-2026-001',
    unidade: 'UBS Jardim',
    quantidade: 250,
    validade: '2026-11-25',
  },
  {
    id: 3,
    medicamento: 'Ibuprofeno 600mg',
    numeroLote: 'IBU-2025-032',
    unidade: 'UBS Sul',
    quantidade: 80,
    validade: '2026-10-20',
  },
  {
    id: 4,
    medicamento: 'Paracetamol 750mg',
    numeroLote: 'PAR-2026-008',
    unidade: 'UBS Norte',
    quantidade: 300,
    validade: '2027-03-15',
  },
  {
    id: 5,
    medicamento: 'Losartana 50mg',
    numeroLote: 'LOS-2026-021',
    unidade: 'UBS Central',
    quantidade: 180,
    validade: '2026-11-15',
  },
]

function calcularDias(validade: string) {
  const hoje = new Date()
  hoje.setHours(0, 0, 0, 0)

  const dataValidade = new Date(`${validade}T00:00:00`)

  return Math.ceil(
    (dataValidade.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24)
  )
}

function getPrioridade(validade: string) {
  const dias = calcularDias(validade)

  if (dias < 0) return 'Vencido'
  if (dias <= 7) return 'Crítico'
  if (dias <= 30) return 'Atenção'

  return 'Normal'
}

function MatrizSalvacao() {
  const [filtro, setFiltro] = useState('Todos')
  const [pesquisa, setPesquisa] = useState('')

  const vencidos = dadosIniciais.filter(
    (lote) => getPrioridade(lote.validade) === 'Vencido'
  ).length

  const criticos = dadosIniciais.filter(
    (lote) => getPrioridade(lote.validade) === 'Crítico'
  ).length

  const atencao = dadosIniciais.filter(
    (lote) => getPrioridade(lote.validade) === 'Atenção'
  ).length

  const lotesFiltrados = dadosIniciais
    .filter((lote) => {
      const prioridade = getPrioridade(lote.validade)

      const correspondeFiltro =
        filtro === 'Todos' || prioridade === filtro

      const correspondePesquisa =
        lote.medicamento.toLowerCase().includes(pesquisa.toLowerCase()) ||
        lote.unidade.toLowerCase().includes(pesquisa.toLowerCase()) ||
        lote.numeroLote.toLowerCase().includes(pesquisa.toLowerCase())

      return correspondeFiltro && correspondePesquisa
    })
    .sort(
      (a, b) => calcularDias(a.validade) - calcularDias(b.validade)
    )

  function formatarData(data: string) {
    return new Date(`${data}T00:00:00`).toLocaleDateString('pt-BR')
  }

  function classePrioridade(prioridade: string) {
    if (prioridade === 'Vencido') {
      return 'bg-red-500/10 text-red-400'
    }

    if (prioridade === 'Crítico') {
      return 'bg-orange-500/10 text-orange-400'
    }

    if (prioridade === 'Atenção') {
      return 'bg-yellow-500/10 text-yellow-400'
    }

    return 'bg-emerald-500/10 text-emerald-400'
  }

  return (
    <div className="min-h-screen text-white">
      <div className="mb-8">
        <p className="mb-1 text-sm text-[#829187]">
          Prevenção de perdas de medicamentos
        </p>

        <h2 className="m-0 text-2xl font-semibold text-white">
          Matriz de Salvação
        </h2>

        <p className="mt-2 text-sm text-[#829187]">
          Identifique lotes em risco e acompanhe os medicamentos que precisam
          de atenção.
        </p>
      </div>

      <div className="mb-8 grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {[
          {
            titulo: 'Lotes monitorados',
            valor: dadosIniciais.length,
            cor: 'text-white',
            icone: '▦',
          },
          {
            titulo: 'Vencidos',
            valor: vencidos,
            cor: 'text-red-400',
            icone: '×',
          },
          {
            titulo: 'Prioridade crítica',
            valor: criticos,
            cor: 'text-orange-400',
            icone: '!',
          },
          {
            titulo: 'Precisam de atenção',
            valor: atencao,
            cor: 'text-yellow-400',
            icone: '◷',
          },
        ].map((item) => (
          <div
            key={item.titulo}
            className="rounded-xl border border-[#293a30] bg-[#111c16] p-5"
          >
            <div className="mb-4 flex items-center justify-between">
              <p className="text-sm text-[#829187]">{item.titulo}</p>
              <span className={`text-xl ${item.cor}`}>{item.icone}</span>
            </div>

            <p className={`text-3xl font-semibold ${item.cor}`}>
              {item.valor}
            </p>
          </div>
        ))}
      </div>

      <div className="mb-6 rounded-xl border border-[#293a30] bg-[#111c16] p-5">
        <h3 className="mb-4 text-lg font-semibold text-white">
          Níveis de prioridade
        </h3>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
          <div className="rounded-lg bg-red-500/5 p-4">
            <span className="text-sm font-semibold text-red-400">
              Vencido
            </span>
            <p className="mt-2 text-xs text-[#a6b3aa]">
              Medicamento com validade ultrapassada.
            </p>
          </div>

          <div className="rounded-lg bg-orange-500/5 p-4">
            <span className="text-sm font-semibold text-orange-400">
              Crítico
            </span>
            <p className="mt-2 text-xs text-[#a6b3aa]">
              Vence em até 7 dias.
            </p>
          </div>

          <div className="rounded-lg bg-yellow-500/5 p-4">
            <span className="text-sm font-semibold text-yellow-400">
              Atenção
            </span>
            <p className="mt-2 text-xs text-[#a6b3aa]">
              Vence entre 8 e 30 dias.
            </p>
          </div>

          <div className="rounded-lg bg-emerald-500/5 p-4">
            <span className="text-sm font-semibold text-emerald-400">
              Normal
            </span>
            <p className="mt-2 text-xs text-[#a6b3aa]">
              Validade acima de 30 dias.
            </p>
          </div>
        </div>
      </div>

      <div className="rounded-xl border border-[#293a30] bg-[#111c16] p-5">
        <div className="mb-6 flex flex-col justify-between gap-4 md:flex-row md:items-center">
          <div>
            <h3 className="text-lg font-semibold text-white">
              Lotes que precisam de atenção
            </h3>

            <p className="mt-1 text-sm text-[#829187]">
              Os lotes são organizados pela proximidade do vencimento.
            </p>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row">
            <input
              type="text"
              placeholder="Buscar medicamento ou UBS..."
              value={pesquisa}
              onChange={(e) => setPesquisa(e.target.value)}
              className="rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-sm text-white outline-none focus:border-[#4b9b69]"
            />

            <select
              value={filtro}
              onChange={(e) => setFiltro(e.target.value)}
              className="rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-sm text-white outline-none"
            >
              <option value="Todos">Todas as prioridades</option>
              <option value="Vencido">Vencidos</option>
              <option value="Crítico">Críticos</option>
              <option value="Atenção">Atenção</option>
              <option value="Normal">Normais</option>
            </select>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[750px] text-left text-sm">
            <thead>
              <tr className="border-b border-[#293a30] text-[#829187]">
                <th className="px-4 py-4 font-medium">Medicamento</th>
                <th className="px-4 py-4 font-medium">Lote</th>
                <th className="px-4 py-4 font-medium">Unidade</th>
                <th className="px-4 py-4 font-medium">Quantidade</th>
                <th className="px-4 py-4 font-medium">Validade</th>
                <th className="px-4 py-4 font-medium">Tempo restante</th>
                <th className="px-4 py-4 font-medium">Prioridade</th>
              </tr>
            </thead>

            <tbody>
              {lotesFiltrados.map((lote) => {
                const dias = calcularDias(lote.validade)
                const prioridade = getPrioridade(lote.validade)

                return (
                  <tr
                    key={lote.id}
                    className="border-b border-[#293a30] last:border-0 hover:bg-[#17251c]"
                  >
                    <td className="px-4 py-5 font-medium text-white">
                      {lote.medicamento}
                    </td>

                    <td className="px-4 py-5 text-[#a6b3aa]">
                      {lote.numeroLote}
                    </td>

                    <td className="px-4 py-5 text-[#a6b3aa]">
                      {lote.unidade}
                    </td>

                    <td className="px-4 py-5 text-[#a6b3aa]">
                      {lote.quantidade} unidades
                    </td>

                    <td className="px-4 py-5 text-[#a6b3aa]">
                      {formatarData(lote.validade)}
                    </td>

                    <td className="px-4 py-5 text-[#a6b3aa]">
                      {dias < 0
                        ? `Vencido há ${Math.abs(dias)} dias`
                        : dias === 0
                          ? 'Vence hoje'
                          : `${dias} dias`}
                    </td>

                    <td className="px-4 py-5">
                      <span
                        className={`rounded-full px-3 py-1 text-xs font-medium ${classePrioridade(prioridade)}`}
                      >
                        {prioridade}
                      </span>
                    </td>
                  </tr>
                )
              })}

              {lotesFiltrados.length === 0 && (
                <tr>
                  <td
                    colSpan={7}
                    className="px-4 py-8 text-center text-[#829187]"
                  >
                    Nenhum lote encontrado.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default MatrizSalvacao
