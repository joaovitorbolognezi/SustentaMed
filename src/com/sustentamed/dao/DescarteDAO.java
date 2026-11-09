package com.sustentamed.dao;

import com.sustentamed.factory.ConnectionFactory;
import com.sustentamed.model.Descarte;
import com.sustentamed.model.EmpresaDescarte;
import com.sustentamed.model.LoteMedicamento;
import com.sustentamed.model.UnidadeSaude;
import com.sustentamed.model.enums.StatusDescarte;
import com.sustentamed.model.enums.StatusLote;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DescarteDAO {

    private final UnidadeSaudeDAO ubsDAO = new UnidadeSaudeDAO();
    private final LoteMedicamentoDAO loteDAO = new LoteMedicamentoDAO();
    private final EmpresaDescarteDAO empDAO = new EmpresaDescarteDAO();

    public void inserir(Descarte descarte) throws SQLException {
        String sql = "INSERT INTO descartes (data_solicitacao, data_coleta, motivo, status, observacao, " +
                     "unidade_saude_id, lote_medicamento_id, empresa_descarte_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(descarte.getDataSolicitacao()));
            ps.setTimestamp(2, descarte.getDataColeta() != null ? Timestamp.valueOf(descarte.getDataColeta()) : null);
            ps.setString(3, descarte.getMotivo());
            ps.setString(4, descarte.getStatus().name());
            ps.setString(5, descarte.getObservacao());
            ps.setInt(6, descarte.getUnidadeSaude().getId());
            ps.setInt(7, descarte.getLoteMedicamento().getId());
            ps.setInt(8, descarte.getEmpresaDescarte().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    descarte.setId(rs.getInt(1));
                }
            }

            // Atualiza status do lote para DESCARTE
            loteDAO.atualizarStatus(descarte.getLoteMedicamento().getId(), StatusLote.DESCARTE);
        }
    }

    public List<Descarte> listarTodos() throws SQLException {
        List<Descarte> lista = new ArrayList<>();
        String sql = "SELECT id, data_solicitacao, data_coleta, motivo, status, observacao, " +
                     "unidade_saude_id, lote_medicamento_id, empresa_descarte_id FROM descartes ORDER BY id DESC";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(instanciarDescarte(rs));
            }
        }
        return lista;
    }

    public void confirmarColeta(int id) throws SQLException {
        String sql = "UPDATE descartes SET status = 'COLETADO', data_coleta = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void cancelar(int id) throws SQLException {
        String sql = "UPDATE descartes SET status = 'CANCELADO' WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Descarte instanciarDescarte(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        LocalDateTime dtSol = rs.getTimestamp("data_solicitacao").toLocalDateTime();
        Timestamp tsCol = rs.getTimestamp("data_coleta");
        LocalDateTime dtCol = (tsCol != null) ? tsCol.toLocalDateTime() : null;
        String motivo = rs.getString("motivo");
        StatusDescarte status = StatusDescarte.valueOf(rs.getString("status"));
        String obs = rs.getString("observacao");
        int ubsId = rs.getInt("unidade_saude_id");
        int loteId = rs.getInt("lote_medicamento_id");
        int empId = rs.getInt("empresa_descarte_id");

        UnidadeSaude ubs = ubsDAO.buscarPorId(ubsId);
        LoteMedicamento lote = loteDAO.buscarPorId(loteId);
        EmpresaDescarte emp = empDAO.buscarPorId(empId);

        return new Descarte(id, dtSol, dtCol, motivo, status, obs, ubs, lote, emp);
    }
}

