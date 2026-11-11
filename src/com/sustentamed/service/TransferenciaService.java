package com.sustentamed.service;

import com.sustentamed.dao.LoteMedicamentoDAO;
import com.sustentamed.dao.SolicitacaoTransferenciaDAO;
import com.sustentamed.dao.UnidadeSaudeDAO;
import com.sustentamed.model.LoteMedicamento;
import com.sustentamed.model.SolicitacaoTransferencia;
import com.sustentamed.model.UnidadeSaude;
import com.sustentamed.model.enums.StatusTransferencia;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TransferenciaService {

    private final SolicitacaoTransferenciaDAO transfDAO = new SolicitacaoTransferenciaDAO();
    private final LoteMedicamentoDAO loteDAO = new LoteMedicamentoDAO();
    private final UnidadeSaudeDAO ubsDAO = new UnidadeSaudeDAO();

    public void solicitarTransferencia(int loteId, int origemId, int destinoId, int quantidade, String observacao) throws SQLException {
        LoteMedicamento lote = loteDAO.buscarPorId(loteId);
        if (lote == null) {
            throw new IllegalArgumentException("Lote de medicamento não encontrado.");
        }
        if (lote.getQuantidade() < quantidade) {
            throw new IllegalArgumentException("Quantidade solicitada (" + quantidade + ") excede o estoque disponível (" + lote.getQuantidade() + ").");
        }
        if (origemId == destinoId) {
            throw new IllegalArgumentException("A unidade de origem e destino não podem ser as mesmas.");
        }

        UnidadeSaude origem = ubsDAO.buscarPorId(origemId);
        UnidadeSaude destino = ubsDAO.buscarPorId(destinoId);

        SolicitacaoTransferencia st = new SolicitacaoTransferencia();
        st.setQuantidade(quantidade);
        st.setDataSolicitacao(LocalDateTime.now());
        st.setStatus(StatusTransferencia.PENDENTE);
        st.setObservacao(observacao);
        st.setUnidadeOrigem(origem);
        st.setUnidadeDestino(destino);
        st.setLoteMedicamento(lote);

        transfDAO.inserir(st);
    }

    public void aprovarTransferencia(int transferenciaId) throws SQLException {
        SolicitacaoTransferencia st = transfDAO.buscarPorId(transferenciaId);
        if (st == null) {
            throw new IllegalArgumentException("Transferência não encontrada.");
        }
        if (st.getStatus() != StatusTransferencia.PENDENTE) {
            throw new IllegalStateException("Apenas transferências pendentes podem ser aprovadas.");
        }

        LoteMedicamento lote = st.getLoteMedicamento();
        if (lote.getQuantidade() < st.getQuantidade()) {
            throw new IllegalStateException("Estoque insuficiente para concluir a transferência.");
        }

        // Deduz da origem
        int novaQtd = lote.getQuantidade() - st.getQuantidade();
        loteDAO.atualizarQuantidade(lote.getId(), novaQtd);

        // Atualiza status da solicitação
        transfDAO.atualizarStatus(transferenciaId, StatusTransferencia.APROVADA);
    }

    public void rejeitarTransferencia(int transferenciaId) throws SQLException {
        SolicitacaoTransferencia st = transfDAO.buscarPorId(transferenciaId);
        if (st == null) {
            throw new IllegalArgumentException("Transferência não encontrada.");
        }
        if (st.getStatus() != StatusTransferencia.PENDENTE) {
            throw new IllegalStateException("Apenas transferências pendentes podem ser rejeitadas.");
        }
        transfDAO.atualizarStatus(transferenciaId, StatusTransferencia.REJEITADA);
    }

    public void cancelarTransferencia(int transferenciaId) throws SQLException {
        SolicitacaoTransferencia st = transfDAO.buscarPorId(transferenciaId);
        if (st == null) {
            throw new IllegalArgumentException("Transferência não encontrada.");
        }
        if (st.getStatus() != StatusTransferencia.PENDENTE) {
            throw new IllegalStateException("Apenas transferências pendentes podem ser canceladas.");
        }
        transfDAO.atualizarStatus(transferenciaId, StatusTransferencia.CANCELADA);
    }

    public List<SolicitacaoTransferencia> listarTodas() throws SQLException {
        return transfDAO.listarTodas();
    }
}

