
import { useCallback, useEffect, useState } from 'react'
import closeIcon from '../../assets/icons/close.svg'
import {
  ApiError,
  atualizarStatusTransferencia,
  criarTransferencia,
  listarLotes,
  listarTransferencias,
  listarUnidades,
  type StatusTransferenciaApi,
} from '../../api/client'
import type { LoteApi, TransferenciaApi, UnidadeApi } from '../../api/types'
import { formatarDataBr, formatarStatusTransferencia } from '../../api/loteUtils'
import { getUsuarioLogado } from '../../api/auth'

type StatusUi = 'Pendente' | 'Aprovada' | 'Rejeitada' | 'Cancelada'

function Transferencias() {
  const [solicitacoes, setSolicitacoes] = useState<TransferenciaApi[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [atualizandoId, setAtualizandoId] = useState<number | null>(null)
  const [filtro, setFiltro] = useState('Todas')
  const [modalAberto, setModalAberto] = useState(false)
  const [salvando, setSalvando] = useState(false)
  const [lotes, setLotes] = useState<LoteApi[]>([])
  const [unidades, setUnidades] = useState<UnidadeApi[]>([])
  const [origemId, setOrigemId] = useState('')
  const [destinoId, setDestinoId] = useState('')
  const [loteId, setLoteId] = useState('')
  const [quantidade, setQuantidade] = useState('')
  const [observacao, setObservacao] = useState('')

  const carregar = useCallback(async () => {
    setCarregando(true)
    setErro('')
    try {
      const lista = await listarTransferencias()
      setSolicitacoes(lista)
    } catch (err) {
      setErro(
        err instanceof ApiError
          ? err.message
          : 'Falha ao carregar as transferências.'
      )
    } finally {
      setCarregando(false)
    }
  }, [])

  useEffect(() => {
    void carregar()
  }, [carregar])

  async function abrirModalNovaSolicitacao() {
    setErro('')
    try {
      const [listaLotes, listaUnidades] = await Promise.all([
        listarLotes(),
        listarUnidades(),
      ])
      setLotes(listaLotes)
      setUnidades(listaUnidades)
      const usuario = getUsuarioLogado()
      const origemPadrao = usuario?.unidade?.id
        ? String(usuario.unidade.id)
        : listaUnidades[0]?.id != null
          ? String(listaUnidades[0].id)
          : ''
      setOrigemId(origemPadrao)
      setDestinoId('')
      setLoteId('')
      setQuantidade('')
      setObservacao('')
      setModalAberto(true)
    } catch (err) {
      setErro(
        err instanceof ApiError
          ? err.message
          : 'Não foi possível abrir o formulário de solicitação.'
      )
    }
  }

  const lotesNaOrigem = lotes.filter(
    (l) => l.unidadeId != null && String(l.unidadeId) === origemId
  )

  const loteSelecionado = lotes.find((l) => String(l.id) === loteId)

  async function enviarSolicitacao(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setSalvando(true)
    setErro('')
    try {
      await criarTransferencia({
        loteId: Number(loteId),
        origemId: Number(origemId),
        destinoId: Number(destinoId),
        quantidade: Number(quantidade),
        observacao: observacao.trim() || undefined,
      })
      setModalAberto(false)
      await carregar()
    } catch (err) {
      setErro(
        err instanceof ApiError
          ? err.message
          : 'Não foi possível criar a solicitação.'
      )
    } finally {
      setSalvando(false)
    }
  }

  async function alterarStatus(id: number, statusApi: StatusTransferenciaApi) {
    setAtualizandoId(id)
    setErro('')
    try {
      await atualizarStatusTransferencia(id, statusApi)
      await carregar()
    } catch (err) {
      setErro(
        err instanceof ApiError
          ? err.message
          : 'Não foi possível atualizar a solicitação.'
      )
    } finally {
      setAtualizandoId(null)
    }
  }

  const solicitacoesFiltradas = solicitacoes.filter((solicitacao) => {
    const statusUi = formatarStatusTransferencia(solicitacao.status) as StatusUi
    return filtro === 'Todas' || statusUi === filtro
  })

  const pendentes = solicitacoes.filter(
    (item) => item.status === 'PENDENTE'
  ).length

  const aprovadas = solicitacoes.filter(
    (item) => item.status === 'APROVADA'
  ).length

  const rejeitadas = solicitacoes.filter(
    (item) => item.status === 'REJEITADA'
  ).length

  function classeStatus(status: string) {
    const statusUi = formatarStatusTransferencia(status) as StatusUi
    const classes: Record<StatusUi, string> = {
      Pendente: 'bg-yellow-500/10 text-yellow-400',
      Aprovada: 'bg-emerald-500/10 text-emerald-400',
      Rejeitada: 'bg-red-500/10 text-red-400',
      Cancelada: 'bg-gray-500/10 text-gray-400',
    }

    return classes[statusUi]
  }

  return (
    <div className="min-h-screen text-white">
      <div className="mb-8 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <p className="mb-1 text-sm text-[#829187]">
            Gestão entre unidades de saúde
          </p>

          <h2 className="m-0 text-2xl font-semibold text-white">
            Solicitações de Transferência
          </h2>

          <p className="mt-2 text-sm text-[#829187]">
            Gerencie as solicitações de medicamentos entre UBS.
          </p>
        </div>

        <button
          type="button"
          onClick={() => void abrirModalNovaSolicitacao()}
          className="rounded-lg bg-[#205b3e] px-5 py-3 text-sm font-semibold text-white transition hover:bg-[#28744c]"
        >
          + Nova solicitação
        </button>
      </div>

      {erro ? (
        <p className="mb-6 rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300" role="alert">
          {erro}
        </p>
      ) : null}

      <div className="mb-8 grid grid-cols-1 gap-4 sm:grid-cols-3">
        {[
          {
            titulo: 'Pendentes',
            valor: pendentes,
            cor: 'text-yellow-400',
          },
          {
            titulo: 'Aprovadas',
            valor: aprovadas,
            cor: 'text-emerald-400',
          },
          {
            titulo: 'Rejeitadas',
            valor: rejeitadas,
            cor: 'text-red-400',
          },
        ].map((item) => (
          <div
            key={item.titulo}
            className="rounded-xl border border-[#293a30] bg-[#111c16] p-5"
          >
            <p className="text-sm text-[#829187]">{item.titulo}</p>
            <p className={`mt-3 text-3xl font-semibold ${item.cor}`}>
              {carregando ? '—' : item.valor}
            </p>
          </div>
        ))}
      </div>

      <div className="rounded-xl border border-[#293a30] bg-[#111c16] p-5">
        <div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <h3 className="text-lg font-semibold text-white">
              Lista de solicitações
            </h3>
            <p className="mt-1 text-sm text-[#829187]">
              Acompanhe e gerencie as transferências.
            </p>
          </div>

          <select
            value={filtro}
            onChange={(e) => setFiltro(e.target.value)}
            className="rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-sm text-white outline-none"
          >
            <option value="Todas">Todas as solicitações</option>
            <option value="Pendente">Pendentes</option>
            <option value="Aprovada">Aprovadas</option>
            <option value="Rejeitada">Rejeitadas</option>
            <option value="Cancelada">Canceladas</option>
          </select>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[900px] text-left text-sm">
            <thead>
              <tr className="border-b border-[#293a30] text-[#829187]">
                <th className="px-4 py-4 font-medium">Medicamento</th>
                <th className="px-4 py-4 font-medium">Origem</th>
                <th className="px-4 py-4 font-medium">Destino</th>
                <th className="px-4 py-4 font-medium">Quantidade</th>
                <th className="px-4 py-4 font-medium">Data</th>
                <th className="px-4 py-4 font-medium">Situação</th>
                <th className="px-4 py-4 font-medium">Ações</th>
              </tr>
            </thead>

            <tbody>
              {carregando ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-[#829187]">
                    Carregando solicitações…
                  </td>
                </tr>
              ) : (
                solicitacoesFiltradas.map((item) => {
                  const statusUi = formatarStatusTransferencia(item.status)

                  return (
                    <tr
                      key={item.id}
                      className="border-b border-[#293a30] last:border-0 hover:bg-[#17251c]"
                    >
                      <td className="px-4 py-5 font-medium text-white">
                        {item.medicamento}
                      </td>

                      <td className="px-4 py-5 text-[#a6b3aa]">
                        {item.origem}
                      </td>

                      <td className="px-4 py-5 text-[#a6b3aa]">
                        {item.destino}
                      </td>

                      <td className="px-4 py-5 text-[#a6b3aa]">
                        {item.quantidade} unidades
                      </td>

                      <td className="px-4 py-5 text-[#a6b3aa]">
                        {formatarDataBr(item.data)}
                      </td>

                      <td className="px-4 py-5">
                        <span
                          className={`rounded-full px-3 py-1 text-xs font-medium ${classeStatus(item.status)}`}
                        >
                          {statusUi}
                        </span>
                      </td>

                      <td className="px-4 py-5">
                        {item.status === 'PENDENTE' ? (
                          <div className="flex gap-2">
                            <button
                              disabled={atualizandoId === item.id}
                              onClick={() => void alterarStatus(item.id, 'APROVADA')}
                              className="rounded-md bg-emerald-500/10 px-3 py-2 text-xs text-emerald-400 hover:bg-emerald-500/20 disabled:opacity-50"
                            >
                              Aprovar
                            </button>

                            <button
                              disabled={atualizandoId === item.id}
                              onClick={() => void alterarStatus(item.id, 'REJEITADA')}
                              className="rounded-md bg-red-500/10 px-3 py-2 text-xs text-red-400 hover:bg-red-500/20 disabled:opacity-50"
                            >
                              Rejeitar
                            </button>

                            <button
                              disabled={atualizandoId === item.id}
                              onClick={() => void alterarStatus(item.id, 'CANCELADA')}
                              className="rounded-md bg-gray-500/10 px-3 py-2 text-xs text-gray-400 hover:bg-gray-500/20 disabled:opacity-50"
                            >
                              Cancelar
                            </button>
                          </div>
                        ) : (
                          <span className="text-xs text-[#829187]">
                            Sem ações disponíveis
                          </span>
                        )}
                      </td>
                    </tr>
                  )
                })
              )}

              {!carregando && solicitacoesFiltradas.length === 0 && (
                <tr>
                  <td
                    colSpan={7}
                    className="px-4 py-8 text-center text-[#829187]"
                  >
                    Nenhuma solicitação encontrada.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {modalAberto && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
          <div className="w-full max-w-lg rounded-xl border border-[#35443a] bg-[#111c16] p-6">
            <div className="mb-6 flex items-center justify-between">
              <h3 className="text-xl font-semibold text-white">
                Nova solicitação entre UBS
              </h3>
              <button
                type="button"
                onClick={() => setModalAberto(false)}
                aria-label="Fechar"
                className="text-[#829187] hover:text-white"
              >
                <img
                  src={closeIcon}
                  alt=""
                  aria-hidden="true"
                  className="ui-icon h-5 w-5"
                />
              </button>
            </div>

            <form onSubmit={enviarSolicitacao} className="flex flex-col gap-4">
              <div>
                <label className="mb-2 block text-sm text-[#a6b3aa]">
                  Unidade de origem
                </label>
                <select
                  required
                  value={origemId}
                  onChange={(e) => {
                    setOrigemId(e.target.value)
                    setLoteId('')
                  }}
                  className="w-full rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-white outline-none focus:border-[#4b9b69]"
                >
                  <option value="">Selecione…</option>
                  {unidades.map((ubs) => (
                    <option key={ubs.id} value={ubs.id}>
                      {ubs.nome}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="mb-2 block text-sm text-[#a6b3aa]">
                  Unidade de destino
                </label>
                <select
                  required
                  value={destinoId}
                  onChange={(e) => setDestinoId(e.target.value)}
                  className="w-full rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-white outline-none focus:border-[#4b9b69]"
                >
                  <option value="">Selecione…</option>
                  {unidades
                    .filter((ubs) => String(ubs.id) !== origemId)
                    .map((ubs) => (
                      <option key={ubs.id} value={ubs.id}>
                        {ubs.nome}
                      </option>
                    ))}
                </select>
              </div>

              <div>
                <label className="mb-2 block text-sm text-[#a6b3aa]">
                  Lote a transferir
                </label>
                <select
                  required
                  value={loteId}
                  onChange={(e) => setLoteId(e.target.value)}
                  className="w-full rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-white outline-none focus:border-[#4b9b69]"
                  disabled={!origemId}
                >
                  <option value="">Selecione…</option>
                  {lotesNaOrigem.map((lote) => (
                    <option key={lote.id} value={lote.id}>
                      {lote.medicamento} — {lote.numeroLote} ({lote.quantidade} un.)
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="mb-2 block text-sm text-[#a6b3aa]">
                  Quantidade
                </label>
                <input
                  required
                  type="number"
                  min={1}
                  max={loteSelecionado?.quantidade ?? undefined}
                  value={quantidade}
                  onChange={(e) => setQuantidade(e.target.value)}
                  placeholder={
                    loteSelecionado
                      ? `Até ${loteSelecionado.quantidade} unidades`
                      : 'Quantidade'
                  }
                  className="w-full rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-white outline-none focus:border-[#4b9b69]"
                />
              </div>

              <div>
                <label className="mb-2 block text-sm text-[#a6b3aa]">
                  Observação (opcional)
                </label>
                <textarea
                  value={observacao}
                  onChange={(e) => setObservacao(e.target.value)}
                  rows={3}
                  placeholder="Ex.: Reposição urgente solicitada pela unidade destino"
                  className="w-full resize-none rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-white outline-none focus:border-[#4b9b69]"
                />
              </div>

              <div className="mt-2 flex justify-end gap-3">
                <button
                  type="button"
                  onClick={() => setModalAberto(false)}
                  className="rounded-lg border border-[#35443a] px-4 py-2 text-sm text-[#a6b3aa] hover:bg-[#1a2920]"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={salvando}
                  className="rounded-lg bg-[#205b3e] px-5 py-2 text-sm font-semibold text-white hover:bg-[#28744c] disabled:opacity-60"
                >
                  {salvando ? 'Enviando…' : 'Solicitar transferência'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default Transferencias
