
import { useCallback, useEffect, useState } from 'react'
import packageIcon from '../../assets/icons/package.svg'
import checkIcon from '../../assets/icons/check.svg'
import alertIcon from '../../assets/icons/alert.svg'
import closeIcon from '../../assets/icons/close.svg'
import {
  ApiError,
  cadastrarLote,
  listarLotes,
  listarMedicamentos,
  listarUnidades,
  obterLote,
} from '../../api/client'
import type { LoteApi, MedicamentoApi, UnidadeApi } from '../../api/types'
import { formatarDataBr, situacaoPorValidade } from '../../api/loteUtils'

function Estoque() {
  const [lotes, setLotes] = useState<LoteApi[]>([])
  const [medicamentos, setMedicamentos] = useState<MedicamentoApi[]>([])
  const [unidades, setUnidades] = useState<UnidadeApi[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  const [pesquisa, setPesquisa] = useState('')
  const [filtro, setFiltro] = useState('Todos')
  const [modalAberto, setModalAberto] = useState(false)
  const [salvando, setSalvando] = useState(false)

  const [medicamentoId, setMedicamentoId] = useState('')
  const [unidadeId, setUnidadeId] = useState('')
  const [numeroLote, setNumeroLote] = useState('')
  const [quantidade, setQuantidade] = useState('')
  const [validade, setValidade] = useState('')
  const [detalheAberto, setDetalheAberto] = useState(false)
  const [detalheCarregando, setDetalheCarregando] = useState(false)
  const [loteDetalhe, setLoteDetalhe] = useState<LoteApi | null>(null)

  const carregarDados = useCallback(async () => {
    setCarregando(true)
    setErro('')
    try {
      const [listaLotes, listaMeds, listaUnidades] = await Promise.all([
        listarLotes(),
        listarMedicamentos(),
        listarUnidades(),
      ])
      setLotes(listaLotes)
      setMedicamentos(listaMeds)
      setUnidades(listaUnidades)
    } catch (err) {
      setErro(
        err instanceof ApiError
          ? err.message
          : 'Falha ao carregar o estoque. Verifique se o servidor HTTP está ativo.'
      )
    } finally {
      setCarregando(false)
    }
  }, [])

  useEffect(() => {
    void carregarDados()
  }, [carregarDados])

  const total = lotes.length
  const disponiveis = lotes.filter(
    (lote) => situacaoPorValidade(lote.validade) === 'Disponível'
  ).length
  const proximos = lotes.filter(
    (lote) => situacaoPorValidade(lote.validade) === 'Próximo do Vencimento'
  ).length
  const vencidos = lotes.filter(
    (lote) => situacaoPorValidade(lote.validade) === 'Vencido'
  ).length

  const lotesFiltrados = lotes.filter((lote) => {
    const correspondePesquisa =
      lote.medicamento.toLowerCase().includes(pesquisa.toLowerCase()) ||
      lote.numeroLote.toLowerCase().includes(pesquisa.toLowerCase())

    const correspondeFiltro =
      filtro === 'Todos' || situacaoPorValidade(lote.validade) === filtro

    return correspondePesquisa && correspondeFiltro
  })

  async function abrirDetalheLote(id: number) {
    setDetalheAberto(true)
    setDetalheCarregando(true)
    setLoteDetalhe(null)
    try {
      setLoteDetalhe(await obterLote(id))
    } catch (err) {
      setErro(
        err instanceof ApiError
          ? err.message
          : 'Não foi possível carregar os detalhes do lote.'
      )
      setDetalheAberto(false)
    } finally {
      setDetalheCarregando(false)
    }
  }

  async function cadastrarLoteForm(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setSalvando(true)
    setErro('')

    try {
      await cadastrarLote({
        medicamentoId: Number(medicamentoId),
        unidadeId: Number(unidadeId),
        numeroLote,
        quantidade: Number(quantidade),
        validade,
      })
      setMedicamentoId('')
      setUnidadeId('')
      setNumeroLote('')
      setQuantidade('')
      setValidade('')
      setModalAberto(false)
      await carregarDados()
    } catch (err) {
      setErro(
        err instanceof ApiError
          ? err.message
          : 'Não foi possível cadastrar o lote.'
      )
    } finally {
      setSalvando(false)
    }
  }

  function classeStatus(status: string) {
    if (status === 'Disponível') {
      return 'bg-emerald-500/10 text-emerald-400'
    }

    if (status === 'Próximo do Vencimento') {
      return 'bg-yellow-500/10 text-yellow-400'
    }

    return 'bg-red-500/10 text-red-400'
  }

  return (
    <div className="min-h-screen text-white">
      <div className="mb-8 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <p className="mb-1 text-sm text-[#829187]">Gestão de medicamentos</p>
          <h2 className="m-0 text-2xl font-semibold text-white">
            Estoque e Lotes
          </h2>
          <p className="mt-2 text-sm text-[#829187]">
            Acompanhe os medicamentos e suas datas de validade.
          </p>
        </div>

        <button
          onClick={() => setModalAberto(true)}
          className="rounded-lg bg-[#205b3e] px-5 py-3 text-sm font-semibold text-white transition hover:bg-[#28744c]"
        >
          + Novo lote
        </button>
      </div>

      {erro ? (
        <p className="mb-6 rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300" role="alert">
          {erro}
        </p>
      ) : null}

      <div className="mb-8 grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {[
          { titulo: 'Total de lotes', valor: total, cor: 'text-white', icone: packageIcon },
          { titulo: 'Disponíveis', valor: disponiveis, cor: 'text-emerald-400', icone: checkIcon },
          { titulo: 'Próximos do vencimento', valor: proximos, cor: 'text-yellow-400', icone: alertIcon },
          { titulo: 'Vencidos', valor: vencidos, cor: 'text-red-400', icone: closeIcon },
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

      <div className="rounded-xl border border-[#293a30] bg-[#111c16] p-5">
        <div className="mb-5 flex flex-col justify-between gap-4 md:flex-row md:items-center">
          <div>
            <h3 className="text-lg font-semibold text-white">
              Lista de medicamentos
            </h3>
            <p className="mt-1 text-sm text-[#829187]">
              Consulte os lotes cadastrados no sistema.
            </p>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row">
            <input
              type="text"
              placeholder="Buscar medicamento ou lote..."
              value={pesquisa}
              onChange={(e) => setPesquisa(e.target.value)}
              className="w-full min-w-0 rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-2 text-sm text-white outline-none focus:border-[#4b9b69] sm:w-64"
            />

            <select
              value={filtro}
              onChange={(e) => setFiltro(e.target.value)}
              className="rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-2 text-sm text-white outline-none"
            >
              <option>Todos</option>
              <option>Disponível</option>
              <option>Próximo do Vencimento</option>
              <option>Vencido</option>
            </select>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[650px] text-left text-sm">
            <thead>
              <tr className="border-b border-[#293a30] text-[#829187]">
                <th className="px-4 py-4 font-medium">Medicamento</th>
                <th className="px-4 py-4 font-medium">Lote</th>
                <th className="px-4 py-4 font-medium">Quantidade</th>
                <th className="px-4 py-4 font-medium">Validade</th>
                <th className="px-4 py-4 font-medium">Unidade</th>
                <th className="px-4 py-4 font-medium">Situação</th>
              </tr>
            </thead>

            <tbody>
              {carregando ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-[#829187]">
                    Carregando lotes…
                  </td>
                </tr>
              ) : (
                lotesFiltrados.map((lote) => {
                  const status = situacaoPorValidade(lote.validade)

                  return (
                    <tr
                      key={lote.id}
                      onClick={() => void abrirDetalheLote(lote.id)}
                      className="cursor-pointer border-b border-[#293a30] last:border-0 hover:bg-[#17251c]"
                    >
                      <td className="px-4 py-5 font-medium text-white">
                        {lote.medicamento}
                      </td>
                      <td className="px-4 py-5 text-[#a6b3aa]">
                        {lote.numeroLote}
                      </td>
                      <td className="px-4 py-5 text-[#a6b3aa]">
                        {lote.quantidade} unidades
                      </td>
                      <td className="px-4 py-5 text-[#a6b3aa]">
                        {formatarDataBr(lote.validade)}
                      </td>
                      <td className="px-4 py-5 text-[#a6b3aa]">
                        {lote.unidade ?? '—'}
                      </td>
                      <td className="px-4 py-5">
                        <span
                          className={`rounded-full px-3 py-1 text-xs font-medium ${classeStatus(status)}`}
                        >
                          {status}
                        </span>
                      </td>
                    </tr>
                  )
                })
              )}

              {!carregando && lotesFiltrados.length === 0 && (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-[#829187]">
                    Nenhum lote encontrado.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {detalheAberto && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
          <div className="w-full max-w-md rounded-xl border border-[#35443a] bg-[#111c16] p-6">
            <div className="mb-6 flex items-center justify-between">
              <h3 className="text-xl font-semibold text-white">Detalhes do lote</h3>
              <button
                type="button"
                onClick={() => setDetalheAberto(false)}
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

            {detalheCarregando ? (
              <p className="text-sm text-[#829187]">Carregando…</p>
            ) : loteDetalhe ? (
              <dl className="grid gap-3 text-sm">
                {[
                  ['Medicamento', loteDetalhe.medicamento],
                  ['Número do lote', loteDetalhe.numeroLote],
                  ['Quantidade', `${loteDetalhe.quantidade} unidades`],
                  ['Validade', formatarDataBr(loteDetalhe.validade)],
                  ['Unidade', loteDetalhe.unidade ?? '—'],
                  ['Status no sistema', loteDetalhe.status ?? '—'],
                  [
                    'Prioridade',
                    loteDetalhe.prioridade ??
                      situacaoPorValidade(loteDetalhe.validade),
                  ],
                  [
                    'Dias até o vencimento',
                    loteDetalhe.diasRestantes != null
                      ? String(loteDetalhe.diasRestantes)
                      : '—',
                  ],
                ].map(([rotulo, valor]) => (
                  <div key={rotulo} className="flex justify-between gap-4 border-b border-[#293a30] pb-2">
                    <dt className="text-[#829187]">{rotulo}</dt>
                    <dd className="text-right font-medium text-white">{valor}</dd>
                  </div>
                ))}
              </dl>
            ) : null}
          </div>
        </div>
      )}

      {modalAberto && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
          <div className="w-full max-w-lg rounded-xl border border-[#35443a] bg-[#111c16] p-6">
            <div className="mb-6 flex items-center justify-between">
              <h3 className="text-xl font-semibold text-white">
                Cadastrar novo lote
              </h3>
              <button
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

            <form onSubmit={cadastrarLoteForm} className="flex flex-col gap-4">
              <div>
                <label className="mb-2 block text-sm text-[#a6b3aa]">
                  Medicamento
                </label>
                <select
                  required
                  value={medicamentoId}
                  onChange={(e) => setMedicamentoId(e.target.value)}
                  className="w-full rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-white outline-none focus:border-[#4b9b69]"
                >
                  <option value="">Selecione…</option>
                  {medicamentos.map((med) => (
                    <option key={med.id} value={med.id}>
                      {med.nome}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="mb-2 block text-sm text-[#a6b3aa]">
                  Unidade de saúde
                </label>
                <select
                  required
                  value={unidadeId}
                  onChange={(e) => setUnidadeId(e.target.value)}
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
                  Número do lote
                </label>
                <input
                  required
                  value={numeroLote}
                  onChange={(e) => setNumeroLote(e.target.value)}
                  placeholder="Ex: DIP-2026-001"
                  className="w-full rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-white outline-none focus:border-[#4b9b69]"
                />
              </div>

              <div>
                <label className="mb-2 block text-sm text-[#a6b3aa]">
                  Quantidade
                </label>
                <input
                  required
                  min="1"
                  type="number"
                  value={quantidade}
                  onChange={(e) => setQuantidade(e.target.value)}
                  placeholder="Quantidade de unidades"
                  className="w-full rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-white outline-none focus:border-[#4b9b69]"
                />
              </div>

              <div>
                <label className="mb-2 block text-sm text-[#a6b3aa]">
                  Data de validade
                </label>
                <input
                  required
                  type="date"
                  value={validade}
                  onChange={(e) => setValidade(e.target.value)}
                  className="w-full rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-white outline-none focus:border-[#4b9b69]"
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
                  {salvando ? 'Salvando…' : 'Cadastrar lote'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default Estoque
