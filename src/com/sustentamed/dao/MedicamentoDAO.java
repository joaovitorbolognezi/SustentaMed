package com.sustentamed.dao;

import com.sustentamed.factory.ConnectionFactory;
import com.sustentamed.model.Medicamento;
import com.sustentamed.model.enums.StatusInsumo;
import com.sustentamed.model.enums.TipoReceita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO {

    public void inserir(Medicamento med) throws SQLException {
        String sql = "INSERT INTO insumos_medicos (nome, descricao, status, tipo_insumo, principio_ativo, " +
                     "dosagem, fabricante, forma_farmaceutica, tipo_receita) " +
                     "VALUES (?, ?, ?, 'MEDICAMENTO', ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, med.getNome());
            ps.setString(2, med.getDescricao());
            ps.setString(3, med.getStatus() != null ? med.getStatus().name() : "ATIVO");
            ps.setString(4, med.getPrincipioAtivo());
            ps.setString(5, med.getDosagem());
            ps.setString(6, med.getFabricante());
            ps.setString(7, med.getFormaFarmaceutica());
            ps.setString(8, med.getTipoReceita() != null ? med.getTipoReceita().name() : "SIMPLES");
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    med.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Medicamento> listarTodos() throws SQLException {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, status, principio_ativo, dosagem, fabricante, forma_farmaceutica, tipo_receita " +
                     "FROM insumos_medicos WHERE tipo_insumo = 'MEDICAMENTO' ORDER BY nome";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(instanciarMedicamento(rs));
            }
        }
        return lista;
    }

    public Medicamento buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, descricao, status, principio_ativo, dosagem, fabricante, forma_farmaceutica, tipo_receita " +
                     "FROM insumos_medicos WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarMedicamento(rs);
                }
            }
        }
        return null;
    }

    private Medicamento instanciarMedicamento(ResultSet rs) throws SQLException {
        return new Medicamento(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("descricao"),
                StatusInsumo.valueOf(rs.getString("status")),
                rs.getString("principio_ativo"),
                rs.getString("dosagem"),
                rs.getString("fabricante"),
                rs.getString("forma_farmaceutica"),
                TipoReceita.valueOf(rs.getString("tipo_receita"))
        );
    }
}

