
import { useState } from 'react'
import packageIcon from '../../assets/icons/package.svg'
import checkIcon from '../../assets/icons/check.svg'
import alertIcon from '../../assets/icons/alert.svg'
import closeIcon from '../../assets/icons/close.svg'

type Lote = {
  id: number
  medicamento: string
  numeroLote: string
  quantidade: number
  validade: string
}

const dadosIniciais: Lote[] = [
  {
    id: 1,
    medicamento: 'Dipirona 500mg',
    numeroLote: 'DIP-2026-001',
    quantidade: 250,
    validade: '2027-05-10',
  },
  {
    id: 2,
    medicamento: 'Amoxicilina 500mg',
    numeroLote: 'AMX-2026-014',
    quantidade: 120,
    validade: '2026-11-20',
  },
  {
    id: 3,
    medicamento: 'Paracetamol 750mg',
    numeroLote: 'PAR-2026-008',
    quantidade: 300,
    validade: '2027-08-15',
  },
  {
    id: 4,
    medicamento: 'Ibuprofeno 600mg',
    numeroLote: 'IBU-2025-032',
    quantidade: 80,
    validade: '2026-10-15',
  },
]

function getStatus(validade: string) {
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

function Estoque() {
  const [lotes, setLotes] = useState<Lote[]>(dadosIniciais)
  const [pesquisa, setPesquisa] = useState('')
  const [filtro, setFiltro] = useState('Todos')
  const [modalAberto, setModalAberto] = useState(false)

  const [medicamento, setMedicamento] = useState('')
  const [numeroLote, setNumeroLote] = useState('')
  const [quantidade, setQuantidade] = useState('')
  const [validade, setValidade] = useState('')

  const total = lotes.length
  const disponiveis = lotes.filter(
    (lote) => getStatus(lote.validade) === 'Disponível'
  ).length
  const proximos = lotes.filter(
    (lote) => getStatus(lote.validade) === 'Próximo do Vencimento'
  ).length
  const vencidos = lotes.filter(
    (lote) => getStatus(lote.validade) === 'Vencido'
  ).length

  const lotesFiltrados = lotes.filter((lote) => {
    const correspondePesquisa =
      lote.medicamento.toLowerCase().includes(pesquisa.toLowerCase()) ||
      lote.numeroLote.toLowerCase().includes(pesquisa.toLowerCase())

    const correspondeFiltro =
      filtro === 'Todos' || getStatus(lote.validade) === filtro

    return correspondePesquisa && correspondeFiltro
  })

  function cadastrarLote(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()

    const novoLote: Lote = {
      id: Date.now(),
      medicamento,
      numeroLote,
      quantidade: Number(quantidade),
      validade,
    }

    setLotes([...lotes, novoLote])
    setMedicamento('')
    setNumeroLote('')
    setQuantidade('')
    setValidade('')
    setModalAberto(false)
  }

  function formatarData(data: string) {
    return new Date(`${data}T00:00:00`).toLocaleDateString('pt-BR')
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
              {item.valor}
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
                <th className="px-4 py-4 font-medium">Situação</th>
              </tr>
            </thead>

            <tbody>
              {lotesFiltrados.map((lote) => {
                const status = getStatus(lote.validade)

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
                      {lote.quantidade} unidades
                    </td>
                    <td className="px-4 py-5 text-[#a6b3aa]">
                      {formatarData(lote.validade)}
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
              })}

              {lotesFiltrados.length === 0 && (
                <tr>
                  <td colSpan={5} className="px-4 py-8 text-center text-[#829187]">
                    Nenhum lote encontrado.
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

            <form onSubmit={cadastrarLote} className="flex flex-col gap-4">
              <div>
                <label className="mb-2 block text-sm text-[#a6b3aa]">
                  Nome do medicamento
                </label>
                <input
                  required
                  value={medicamento}
                  onChange={(e) => setMedicamento(e.target.value)}
                  placeholder="Ex: Dipirona 500mg"
                  className="w-full rounded-lg border border-[#35443a] bg-[#0e1712] px-4 py-3 text-white outline-none focus:border-[#4b9b69]"
                />
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
                  className="rounded-lg bg-[#205b3e] px-5 py-2 text-sm font-semibold text-white hover:bg-[#28744c]"
                >
                  Cadastrar lote
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
