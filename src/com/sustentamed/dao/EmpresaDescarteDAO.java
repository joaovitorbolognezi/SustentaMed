package com.sustentamed.dao;

import com.sustentamed.factory.ConnectionFactory;
import com.sustentamed.model.EmpresaDescarte;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDescarteDAO {

    public void inserir(EmpresaDescarte emp) throws SQLException {
        String sql = "INSERT INTO empresas_descarte (razao_social, cnpj, telefone, endereco, licenca, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, emp.getRazaoSocial());
            ps.setString(2, emp.getCnpj());
            ps.setString(3, emp.getTelefone());
            ps.setString(4, emp.getEndereco());
            ps.setString(5, emp.getLicenca());
            ps.setBoolean(6, emp.isAtivo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    emp.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<EmpresaDescarte> listarTodas() throws SQLException {
        List<EmpresaDescarte> lista = new ArrayList<>();
        String sql = "SELECT id, razao_social, cnpj, telefone, endereco, licenca, ativo FROM empresas_descarte WHERE ativo = TRUE";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new EmpresaDescarte(
                        rs.getInt("id"),
                        rs.getString("razao_social"),
                        rs.getString("cnpj"),
                        rs.getString("telefone"),
                        rs.getString("endereco"),
                        rs.getString("licenca"),
                        rs.getBoolean("ativo")
                ));
            }
        }
        return lista;
    }

    public EmpresaDescarte buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, razao_social, cnpj, telefone, endereco, licenca, ativo FROM empresas_descarte WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new EmpresaDescarte(
                            rs.getInt("id"),
                            rs.getString("razao_social"),
                            rs.getString("cnpj"),
                            rs.getString("telefone"),
                            rs.getString("endereco"),
                            rs.getString("licenca"),
                            rs.getBoolean("ativo")
                    );
                }
            }
        }
        return null;
    }
}

