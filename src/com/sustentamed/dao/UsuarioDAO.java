package com.sustentamed.dao;

import com.sustentamed.factory.ConnectionFactory;
import com.sustentamed.model.Enfermeira;
import com.sustentamed.model.Farmaceutica;
import com.sustentamed.model.UnidadeSaude;
import com.sustentamed.model.Usuario;
import com.sustentamed.model.enums.TipoUsuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final UnidadeSaudeDAO unidadeDAO = new UnidadeSaudeDAO();

    public Usuario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT id, nome, cpf, email, senha, ativo, tipo_usuario, crf, coren, unidade_saude_id " +
                     "FROM usuarios WHERE email = ? AND senha = ? AND ativo = TRUE";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarUsuario(rs);
                }
            }
        }
        return null;
    }

    public void inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, cpf, email, senha, ativo, tipo_usuario, crf, coren, unidade_saude_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getCpf());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getSenha());
            ps.setBoolean(5, usuario.isAtivo());
            ps.setString(6, usuario.getTipoUsuario().name());

            if (usuario instanceof Farmaceutica) {
                ps.setString(7, ((Farmaceutica) usuario).getCrf());
                ps.setNull(8, Types.VARCHAR);
            } else if (usuario instanceof Enfermeira) {
                ps.setNull(7, Types.VARCHAR);
                ps.setString(8, ((Enfermeira) usuario).getCoren());
            } else {
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.VARCHAR);
            }

            if (usuario.getUnidadeSaude() != null) {
                ps.setInt(9, usuario.getUnidadeSaude().getId());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, email, senha, ativo, tipo_usuario, crf, coren, unidade_saude_id FROM usuarios";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(instanciarUsuario(rs));
            }
        }
        return lista;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, cpf, email, senha, ativo, tipo_usuario, crf, coren, unidade_saude_id FROM usuarios WHERE id = ?";
        try (Connection con = ConnectionFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instanciarUsuario(rs);
                }
            }
        }
        return null;
    }

    private Usuario instanciarUsuario(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String cpf = rs.getString("cpf");
        String email = rs.getString("email");
        String senha = rs.getString("senha");
        boolean ativo = rs.getBoolean("ativo");
        String tipoStr = rs.getString("tipo_usuario");
        TipoUsuario tipo = TipoUsuario.valueOf(tipoStr);
        String crf = rs.getString("crf");
        String coren = rs.getString("coren");
        int unidadeId = rs.getInt("unidade_saude_id");

        UnidadeSaude ubs = null;
        if (unidadeId > 0) {
            ubs = unidadeDAO.buscarPorId(unidadeId);
        }

        if (tipo == TipoUsuario.FARMACEUTICA) {
            return new Farmaceutica(id, nome, cpf, email, senha, ativo, ubs, crf);
        } else if (tipo == TipoUsuario.ENFERMEIRA) {
            return new Enfermeira(id, nome, cpf, email, senha, ativo, ubs, coren);
        } else {
            return new Usuario(id, nome, cpf, email, senha, ativo, tipo, ubs);
        }
    }
}

