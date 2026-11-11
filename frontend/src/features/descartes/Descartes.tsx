
import { useCallback, useEffect, useState } from 'react'
import alertIcon from '../../assets/icons/alert.svg'
import checkIcon from '../../assets/icons/check.svg'
import clockIcon from '../../assets/icons/clock.svg'
import closeIcon from '../../assets/icons/close.svg'
import { ApiError, listarDescartes } from '../../api/client'
import type { DescarteApi } from '../../api/types'
import { formatarDataBr, formatarStatusDescarte } from '../../api/loteUtils'

function classeStatus(status: string) {
  const ui = formatarStatusDescarte(status)
  if (ui === 'Solicitado') return 'bg-yellow-500/10 text-yellow-400'
  if (ui === 'Coletado' || ui === 'Concluído') return 'bg-emerald-500/10 text-emerald-400'
  if (ui === 'Cancelado') return 'bg-gray-500/10 text-gray-400'
  return 'bg-red-500/10 text-red-400'
}

function Descartes() {
  const [descartes, setDescartes] = useState<DescarteApi[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [filtro, setFiltro] = useState('Todos')
  const [pesquisa, setPesquisa] = useState('')

  const carregar = useCallback(async () => {
    setCarregando(true)
    setErro('')
    try {
      setDescartes(await listarDescartes())
    } catch (err) {
      setErro(
        err instanceof ApiError
          ? err.message
          : 'Falha ao carregar descartes ecológicos.'
      )
    } finally {
      setCarregando(false)
    }
  }, [])

  useEffect(() => {
    void carregar()
  }, [carregar])

  const solicitados = descartes.filter((d) => d.status === 'SOLICITADO').length
  const coletados = descartes.filter(
    (d) => d.status === 'COLETADO' || d.status === 'CONCLUIDO'
  ).length
  const cancelados = descartes.filter((d) => d.status === 'CANCELADO').length

  const filtrados = descartes.filter((item) => {
    const statusUi = formatarStatusDescarte(item.status)
    const correspondeFiltro =
      filtro === 'Todos' || statusUi === filtro || item.status === filtro

    const termo = pesquisa.toLowerCase()
    const correspondePesquisa =
      item.medicamento.toLowerCase().includes(termo) ||
      item.numeroLote.toLowerCase().includes(termo) ||
      item.unidade.toLowerCase().includes(termo) ||
      item.empresa.toLowerCase().includes(termo) ||
      item.motivo.toLowerCase().includes(termo)

    return correspondeFiltro && correspondePesquisa
  })

  return (
    <div className="min-h-screen text-white">
      <div className="mb-8">
        <p className="mb-1 text-sm text-[#829187]">
          Destinação ambiental de medicamentos
        </p>
        <h2 className="m-0 text-2xl font-semibold text-white">
          Descartes Ecológicos
        </h2>
        <p className="mt-2 text-sm text-[#829187]">
          Acompanhe o encaminhamento de lotes vencidos para empresas credenciadas.
        </p>
      </div>

      {erro ? (
        <p
          className="mb-6 rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300"
          role="alert"
        >
          {erro}
        </p>
      ) : null}

      <div className="mb-8 grid grid-cols-1 gap-4 sm:grid-cols-3">
        {[
          { titulo: 'Solicitados', valor: solicitados, cor: 'text-yellow-400', icone: clockIcon },
          { titulo: 'Coletados', valor: coletados, cor: 'text-emerald-400', icone: checkIcon },
          { titulo: 'Cancelados', valor: cancelados, cor: 'text-gray-400', icone: closeIcon },
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
        <div className="flex items-start gap-3">
          <img src={alertIcon} alt="" aria-hidden="true" className="ui-icon mt-0.5 h-5 w-5 text-yellow-400" />
          <div>
            <h3 className="text-sm font-semibold text-white">Rastreabilidade ambiental</h3>
            <p className="mt-1 text-xs text-[#829187]">
              Cada registro vincula lote, unidade de saúde e empresa de incineração licenciada,
              conforme o fluxo de descarte seguro do SustentaMed.
            </p>
          </div>
        </div>
      </div>

      <div className="rounded-xl border border-[#293a30] bg-[#111c16] p-5">
        <div className="mb-6 flex flex-col justify-between gap-4 md:flex-row md:items-center">
          <div>
            <h3 className="text-lg font-semibold text-white">Histórico de descartes</h3>
            <p className="mt-1 text-sm text-[#829187]">
              Consulte motivos, status e empresas responsáveis pela coleta.
            </p>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row">
            <input
              type="text"
              placeholder="Buscar medicamento, UBS ou empresa..."
              value={pesquisa}
              onChange={(e) => setPesquisa(e.target.value)}
              className="w-full min-w-0 rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-2 text-sm text-white outline-none focus:border-[#4b9b69] sm:w-64"
            />
            <select
              value={filtro}
              onChange={(e) => setFiltro(e.target.value)}
              className="rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-sm text-white outline-none"
            >
              <option value="Todos">Todos os status</option>
              <option value="Solicitado">Solicitados</option>
              <option value="Coletado">Coletados</option>
              <option value="Concluído">Concluídos</option>
              <option value="Cancelado">Cancelados</option>
            </select>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[950px] text-left text-sm">
            <thead>
              <tr className="border-b border-[#293a30] text-[#829187]">
                <th className="px-4 py-4 font-medium">Medicamento</th>
                <th className="px-4 py-4 font-medium">Lote</th>
                <th className="px-4 py-4 font-medium">UBS</th>
                <th className="px-4 py-4 font-medium">Empresa coletora</th>
                <th className="px-4 py-4 font-medium">Motivo</th>
                <th className="px-4 py-4 font-medium">Data</th>
                <th className="px-4 py-4 font-medium">Status</th>
              </tr>
            </thead>
            <tbody>
              {carregando ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-[#829187]">
                    Carregando descartes…
                  </td>
                </tr>
              ) : (
                filtrados.map((item) => (
                  <tr
                    key={item.id}
                    className="border-b border-[#293a30] last:border-0 hover:bg-[#17251c]"
                  >
                    <td className="px-4 py-5 font-medium text-white">{item.medicamento}</td>
                    <td className="px-4 py-5 text-[#a6b3aa]">{item.numeroLote}</td>
                    <td className="px-4 py-5 text-[#a6b3aa]">{item.unidade}</td>
                    <td className="px-4 py-5 text-[#a6b3aa]">{item.empresa}</td>
                    <td className="px-4 py-5 text-[#a6b3aa]">{item.motivo}</td>
                    <td className="px-4 py-5 text-[#a6b3aa]">{formatarDataBr(item.data)}</td>
                    <td className="px-4 py-5">
                      <span
                        className={`rounded-full px-3 py-1 text-xs font-medium ${classeStatus(item.status)}`}
                      >
                        {formatarStatusDescarte(item.status)}
                      </span>
                    </td>
                  </tr>
                ))
              )}

              {!carregando && filtrados.length === 0 && (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-[#829187]">
                    Nenhum descarte registrado.
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

export default Descartes
