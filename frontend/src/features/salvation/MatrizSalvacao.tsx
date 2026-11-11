
import { useCallback, useEffect, useState } from 'react'
import packageIcon from '../../assets/icons/package.svg'
import closeIcon from '../../assets/icons/close.svg'
import alertIcon from '../../assets/icons/alert.svg'
import clockIcon from '../../assets/icons/clock.svg'
import { ApiError, listarMatrizSalvacao } from '../../api/client'
import type { LoteMatrizApi } from '../../api/types'
import { formatarDataBr } from '../../api/loteUtils'

function MatrizSalvacao() {
  const [lotes, setLotes] = useState<LoteMatrizApi[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [filtro, setFiltro] = useState('Todos')
  const [pesquisa, setPesquisa] = useState('')

  const carregar = useCallback(async () => {
    setCarregando(true)
    setErro('')
    try {
      const lista = await listarMatrizSalvacao()
      setLotes(lista)
    } catch (err) {
      setErro(
        err instanceof ApiError
          ? err.message
          : 'Falha ao carregar a matriz de salvação.'
      )
    } finally {
      setCarregando(false)
    }
  }, [])

  useEffect(() => {
    void carregar()
  }, [carregar])

  const vencidos = lotes.filter((lote) => lote.prioridade === 'Vencido').length
  const criticos = lotes.filter((lote) => lote.prioridade === 'Crítico').length
  const atencao = lotes.filter((lote) => lote.prioridade === 'Atenção').length

  const lotesFiltrados = lotes
    .filter((lote) => {
      const correspondeFiltro =
        filtro === 'Todos' || lote.prioridade === filtro

      const correspondePesquisa =
        lote.medicamento.toLowerCase().includes(pesquisa.toLowerCase()) ||
        (lote.unidade ?? '').toLowerCase().includes(pesquisa.toLowerCase()) ||
        lote.numeroLote.toLowerCase().includes(pesquisa.toLowerCase())

      return correspondeFiltro && correspondePesquisa
    })
    .sort((a, b) => a.diasRestantes - b.diasRestantes)

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

  function textoTempoRestante(dias: number) {
    if (dias < 0) return `Vencido há ${Math.abs(dias)} dias`
    if (dias === 0) return 'Vence hoje'
    return `${dias} dias`
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

      {erro ? (
        <p className="mb-6 rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300" role="alert">
          {erro}
        </p>
      ) : null}

      <div className="mb-8 grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {[
          {
            titulo: 'Lotes monitorados',
            valor: lotes.length,
            cor: 'text-white',
            icone: packageIcon,
          },
          {
            titulo: 'Vencidos',
            valor: vencidos,
            cor: 'text-red-400',
            icone: closeIcon,
          },
          {
            titulo: 'Prioridade crítica',
            valor: criticos,
            cor: 'text-orange-400',
            icone: alertIcon,
          },
          {
            titulo: 'Precisam de atenção',
            valor: atencao,
            cor: 'text-yellow-400',
            icone: clockIcon,
          },
        ].map((item) => (
          <div
            key={item.titulo}
            className="rounded-xl border border-[#293a30] bg-[#111c16] p-5"
          >
            <div className="mb-4 flex items-center justify-between">
              <p className="text-sm text-[#829187]">{item.titulo}</p>
              <img
                src={item.icone}
                alt=""
                aria-hidden="true"
                className={`ui-icon h-5 w-5 ${item.cor}`}
              />
            </div>

            <p className={`text-3xl font-semibold ${item.cor}`}>
              {carregando ? '—' : item.valor}
            </p>
          </div>
        ))}
      </div>

      <div className="mb-6 rounded-xl border border-[#293a30] bg-[#111c16] p-5">
        <h3 className="mb-4 text-lg font-semibold text-white">
          Níveis de prioridade
        </h3>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
          <div className="priority-level priority-level-red rounded-lg border border-transparent bg-red-500/5 p-4">
            <span className="text-sm font-semibold text-red-400">
              Vencido
            </span>
            <p className="mt-2 text-xs text-[#a6b3aa]">
              Medicamento com validade ultrapassada.
            </p>
          </div>

          <div className="priority-level priority-level-orange rounded-lg border border-transparent bg-orange-500/5 p-4">
            <span className="text-sm font-semibold text-orange-400">
              Crítico
            </span>
            <p className="mt-2 text-xs text-[#a6b3aa]">
              Vence em até 7 dias.
            </p>
          </div>

          <div className="priority-level priority-level-yellow rounded-lg border border-transparent bg-yellow-500/5 p-4">
            <span className="text-sm font-semibold text-yellow-400">
              Atenção
            </span>
            <p className="mt-2 text-xs text-[#a6b3aa]">
              Vence entre 8 e 30 dias.
            </p>
          </div>

          <div className="priority-level priority-level-normal rounded-lg border border-transparent bg-emerald-500/5 p-4">
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

          <div className="flex flex-col gap-3 lg:flex-row">
            <input
              type="text"
              placeholder="Buscar medicamento ou UBS..."
              value={pesquisa}
              onChange={(e) => setPesquisa(e.target.value)}
              className="w-full min-w-0 rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-2 text-sm text-white outline-none focus:border-[#4b9b69] sm:w-64"
            />

            <select
              value={filtro}
              onChange={(e) => setFiltro(e.target.value)}
              className="w-full rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-sm text-white outline-none lg:w-auto"
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
              {carregando ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-[#829187]">
                    Carregando matriz…
                  </td>
                </tr>
              ) : (
                lotesFiltrados.map((lote) => (
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
                      {lote.unidade ?? '—'}
                    </td>

                    <td className="px-4 py-5 text-[#a6b3aa]">
                      {lote.quantidade} unidades
                    </td>

                    <td className="px-4 py-5 text-[#a6b3aa]">
                      {formatarDataBr(lote.validade)}
                    </td>

                    <td className="px-4 py-5 text-[#a6b3aa]">
                      {textoTempoRestante(lote.diasRestantes)}
                    </td>

                    <td className="px-4 py-5">
                      <span
                        className={`rounded-full px-3 py-1 text-xs font-medium ${classePrioridade(lote.prioridade)}`}
                      >
                        {lote.prioridade}
                      </span>
                    </td>
                  </tr>
                ))
              )}

              {!carregando && lotesFiltrados.length === 0 && (
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
