
import { useState } from 'react'

type Solicitacao = {
  id: number
  medicamento: string
  origem: string
  destino: string
  quantidade: number
  data: string
  status: 'Pendente' | 'Aprovada' | 'Rejeitada' | 'Cancelada'
}

const dadosIniciais: Solicitacao[] = [
  {
    id: 1,
    medicamento: 'Dipirona 500mg',
    origem: 'UBS Central',
    destino: 'UBS Jardim',
    quantidade: 100,
    data: '04/11/2026',
    status: 'Pendente',
  },
  {
    id: 2,
    medicamento: 'Amoxicilina 500mg',
    origem: 'UBS Jardim',
    destino: 'UBS Central',
    quantidade: 50,
    data: '03/11/2026',
    status: 'Pendente',
  },
  {
    id: 3,
    medicamento: 'Paracetamol 750mg',
    origem: 'UBS Norte',
    destino: 'UBS Central',
    quantidade: 80,
    data: '02/11/2026',
    status: 'Aprovada',
  },
  {
    id: 4,
    medicamento: 'Ibuprofeno 600mg',
    origem: 'UBS Central',
    destino: 'UBS Sul',
    quantidade: 40,
    data: '01/11/2026',
    status: 'Rejeitada',
  },
]

function Transferencias() {
  const [solicitacoes, setSolicitacoes] =
    useState<Solicitacao[]>(dadosIniciais)

  const [filtro, setFiltro] = useState('Todas')

  function atualizarStatus(
    id: number,
    novoStatus: Solicitacao['status']
  ) {
    setSolicitacoes((listaAtual) =>
      listaAtual.map((solicitacao) =>
        solicitacao.id === id
          ? { ...solicitacao, status: novoStatus }
          : solicitacao
      )
    )
  }

  const solicitacoesFiltradas = solicitacoes.filter(
    (solicitacao) =>
      filtro === 'Todas' || solicitacao.status === filtro
  )

  const pendentes = solicitacoes.filter(
    (item) => item.status === 'Pendente'
  ).length

  const aprovadas = solicitacoes.filter(
    (item) => item.status === 'Aprovada'
  ).length

  const rejeitadas = solicitacoes.filter(
    (item) => item.status === 'Rejeitada'
  ).length

  function classeStatus(status: Solicitacao['status']) {
    const classes = {
      Pendente: 'bg-yellow-500/10 text-yellow-400',
      Aprovada: 'bg-emerald-500/10 text-emerald-400',
      Rejeitada: 'bg-red-500/10 text-red-400',
      Cancelada: 'bg-gray-500/10 text-gray-400',
    }

    return classes[status]
  }

  return (
    <div className="min-h-screen text-white">
      <div className="mb-8">
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
              {item.valor}
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
              {solicitacoesFiltradas.map((item) => (
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
                    {item.data}
                  </td>

                  <td className="px-4 py-5">
                    <span
                      className={`rounded-full px-3 py-1 text-xs font-medium ${classeStatus(item.status)}`}
                    >
                      {item.status}
                    </span>
                  </td>

                  <td className="px-4 py-5">
                    {item.status === 'Pendente' ? (
                      <div className="flex gap-2">
                        <button
                          onClick={() =>
                            atualizarStatus(item.id, 'Aprovada')
                          }
                          className="rounded-md bg-emerald-500/10 px-3 py-2 text-xs text-emerald-400 hover:bg-emerald-500/20"
                        >
                          Aprovar
                        </button>

                        <button
                          onClick={() =>
                            atualizarStatus(item.id, 'Rejeitada')
                          }
                          className="rounded-md bg-red-500/10 px-3 py-2 text-xs text-red-400 hover:bg-red-500/20"
                        >
                          Rejeitar
                        </button>

                        <button
                          onClick={() =>
                            atualizarStatus(item.id, 'Cancelada')
                          }
                          className="rounded-md bg-gray-500/10 px-3 py-2 text-xs text-gray-400 hover:bg-gray-500/20"
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
              ))}

              {solicitacoesFiltradas.length === 0 && (
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
    </div>
  )
}

export default Transferencias
