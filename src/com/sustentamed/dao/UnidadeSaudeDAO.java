package com.sustentamed.dao;

import com.sustentamed.factory.ConnectionFactory;
import com.sustentamed.model.UnidadeSaude;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnidadeSaudeDAO {

    public void inserir(UnidadeSaude unidade) throws SQLException {
        String sql = "INSERT INTO unidades_saude (cnes, nome, endereco, telefone, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, unidade.getCnes());
            ps.setString(2, unidade.getNome());
            ps.setString(3, unidade.getEndereco());
            ps.setString(4, unidade.getTelefone());
            ps.setBoolean(5, unidade.isAtivo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    unidade.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<UnidadeSaude> listarTodas() throws SQLException {
        List<UnidadeSaude> lista = new ArrayList<>();
        String sql = "SELECT id, cnes, nome, endereco, telefone, ativo FROM unidades_saude ORDER BY nome";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UnidadeSaude u = new UnidadeSaude(
                        rs.getInt("id"),
                        rs.getString("cnes"),
                        rs.getString("nome"),
                        rs.getString("endereco"),
                        rs.getString("telefone"),
                        rs.getBoolean("ativo")
                );
                lista.add(u);
            }
        }
        return lista;
    }

    public UnidadeSaude buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, cnes, nome, endereco, telefone, ativo FROM unidades_saude WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UnidadeSaude(
                            rs.getInt("id"),
                            rs.getString("cnes"),
                            rs.getString("nome"),
                            rs.getString("endereco"),
                            rs.getString("telefone"),
                            rs.getBoolean("ativo")
                    );
                }
            }
        }
        return null;
    }
}

