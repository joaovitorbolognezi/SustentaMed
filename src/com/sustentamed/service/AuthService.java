package com.sustentamed.service;

import com.sustentamed.dao.UsuarioDAO;
import com.sustentamed.model.Usuario;
import java.sql.SQLException;

public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String email, String senha) throws SQLException {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Email e senha são obrigatórios.");
        }
        return usuarioDAO.autenticar(email, senha);
    }
}

