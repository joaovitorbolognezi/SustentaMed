package com.sustentamed.dao;

import com.sustentamed.factory.ConnectionFactory;
import com.sustentamed.model.LoteMedicamento;
import com.sustentamed.model.SolicitacaoTransferencia;
import com.sustentamed.model.UnidadeSaude;
import com.sustentamed.model.enums.StatusTransferencia;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoTransferenciaDAO {

    private final UnidadeSaudeDAO ubsDAO = new UnidadeSaudeDAO();
    private final LoteMedicamentoDAO loteDAO = new LoteMedicamentoDAO();

    public void inserir(SolicitacaoTransferencia st) throws SQLException {
        String sql = "INSERT INTO solicitacoes_transferencia (quantidade, data_solicitacao, status, observacao, " +
                     "unidade_origem_id, unidade_destino_id, lote_medicamento_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, st.getQuantidade());
            ps.setTimestamp(2, Timestamp.valueOf(st.getDataSolicitacao()));
            ps.setString(3, st.getStatus().name());
            ps.setString(4, st.getObservacao());
            ps.setInt(5, st.getUnidadeOrigem().getId());
            ps.setInt(6, st.getUnidadeDestino().getId());
            ps.setInt(7, st.getLoteMedicamento().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    st.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<SolicitacaoTransferencia> listarTodas() throws SQLException {
        List<SolicitacaoTransferencia> lista = new ArrayList<>();
        String sql = "SELECT id, quantidade, data_solicitacao, status, observacao, " +
                     "unidade_origem_id, unidade_destino_id, lote_medicamento_id FROM solicitacoes_transferencia ORDER BY id DESC";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(instanciarTransferencia(rs));
            }
        }
        return lista;
    }

    public SolicitacaoTransferencia buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, quantidade, data_solicitacao, status, observacao, " +
                     "unidade_origem_id, unidade_destino_id, lote_medicamento_id FROM solicitacoes_transferencia WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarTransferencia(rs);
                }
            }
        }
        return null;
    }

    public void atualizarStatus(int id, StatusTransferencia status) throws SQLException {
        String sql = "UPDATE solicitacoes_transferencia SET status = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private SolicitacaoTransferencia instanciarTransferencia(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int qtd = rs.getInt("quantidade");
        Timestamp ts = rs.getTimestamp("data_solicitacao");
        LocalDateTime dtSol = ts.toLocalDateTime();
        String statusStr = rs.getString("status");
        String obs = rs.getString("observacao");
        int origemId = rs.getInt("unidade_origem_id");
        int destinoId = rs.getInt("unidade_destino_id");
        int loteId = rs.getInt("lote_medicamento_id");

        UnidadeSaude origem = ubsDAO.buscarPorId(origemId);
        UnidadeSaude destino = ubsDAO.buscarPorId(destinoId);
        LoteMedicamento lote = loteDAO.buscarPorId(loteId);

        return new SolicitacaoTransferencia(
                id, qtd, dtSol, StatusTransferencia.valueOf(statusStr),
                obs, origem, destino, lote
        );
    }
}

