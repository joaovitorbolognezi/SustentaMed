package com.sustentamed.dao;

import com.sustentamed.factory.ConnectionFactory;
import com.sustentamed.model.LoteMedicamento;
import com.sustentamed.model.Medicamento;
import com.sustentamed.model.UnidadeSaude;
import com.sustentamed.model.enums.StatusLote;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LoteMedicamentoDAO {

    private final MedicamentoDAO medDAO = new MedicamentoDAO();
    private final UnidadeSaudeDAO ubsDAO = new UnidadeSaudeDAO();

    public void inserir(LoteMedicamento lote) throws SQLException {
        lote.atualizarStatusAutomatico();
        String sql = "INSERT INTO lotes_medicamento (numero_lote, quantidade, data_fabricacao, data_validade, " +
                     "status, medicamento_id, unidade_saude_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, lote.getNumeroLote());
            ps.setInt(2, lote.getQuantidade());
            ps.setDate(3, lote.getDataFabricacao() != null ? Date.valueOf(lote.getDataFabricacao()) : null);
            ps.setDate(4, Date.valueOf(lote.getDataValidade()));
            ps.setString(5, lote.getStatus().name());
            ps.setInt(6, lote.getMedicamento().getId());
            ps.setInt(7, lote.getUnidadeSaude().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    lote.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<LoteMedicamento> listarTodos() throws SQLException {
        List<LoteMedicamento> lista = new ArrayList<>();
        String sql = "SELECT id, numero_lote, quantidade, data_fabricacao, data_validade, status, " +
                     "medicamento_id, unidade_saude_id FROM lotes_medicamento ORDER BY data_validade ASC";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(instanciarLote(rs));
            }
        }
        return lista;
    }

    public List<LoteMedicamento> listarPorUnidade(int unidadeId) throws SQLException {
        List<LoteMedicamento> lista = new ArrayList<>();
        String sql = "SELECT id, numero_lote, quantidade, data_fabricacao, data_validade, status, " +
                     "medicamento_id, unidade_saude_id FROM lotes_medicamento WHERE unidade_saude_id = ? ORDER BY data_validade ASC";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, unidadeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(instanciarLote(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Retorna os lotes priorizados para a Matriz de Salvacao:
     * Lotes criticos (<= 7 dias) e em atencao (<= 30 dias), ordenados pela urgencia de redistribuicao.
     */
    public List<LoteMedicamento> listarMatrizSalvacao() throws SQLException {
        List<LoteMedicamento> todos = listarTodos();
        List<LoteMedicamento> matriz = new ArrayList<>();

        for (LoteMedicamento lote : todos) {
            // Se nao estiver vencido ou se estiver prestes a vencer
            if (lote.isProxVencimento() || lote.isVencido()) {
                matriz.add(lote);
            }
        }

        // Ordena por dias restantes (os mais urgentes primeiro)
        Collections.sort(matriz, Comparator.comparingInt(LoteMedicamento::diasParaVencimento));
        return matriz;
    }

    public LoteMedicamento buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, numero_lote, quantidade, data_fabricacao, data_validade, status, " +
                     "medicamento_id, unidade_saude_id FROM lotes_medicamento WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarLote(rs);
                }
            }
        }
        return null;
    }

    public void atualizarQuantidade(int id, int novaQtd) throws SQLException {
        String sql = "UPDATE lotes_medicamento SET quantidade = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, novaQtd);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void atualizarStatus(int id, StatusLote status) throws SQLException {
        String sql = "UPDATE lotes_medicamento SET status = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private LoteMedicamento instanciarLote(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String numero = rs.getString("numero_lote");
        int qtd = rs.getInt("quantidade");
        Date dtFab = rs.getDate("data_fabricacao");
        Date dtVal = rs.getDate("data_validade");
        String statusStr = rs.getString("status");
        int medId = rs.getInt("medicamento_id");
        int ubsId = rs.getInt("unidade_saude_id");

        LocalDate localFab = (dtFab != null) ? dtFab.toLocalDate() : null;
        LocalDate localVal = dtVal.toLocalDate();

        Medicamento med = medDAO.buscarPorId(medId);
        UnidadeSaude ubs = ubsDAO.buscarPorId(ubsId);

        LoteMedicamento lote = new LoteMedicamento(
                id, numero, qtd, localFab, localVal,
                StatusLote.valueOf(statusStr), med, ubs
        );
        lote.atualizarStatusAutomatico();
        return lote;
    }
}

