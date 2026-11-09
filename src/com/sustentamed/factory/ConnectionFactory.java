package com.sustentamed.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/sistema_saude?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = ""; // Altere aqui se o seu MySQL tiver senha

    public static Connection getConexao() throws SQLException {
        String url = System.getProperty("db.url", DEFAULT_URL);
        String user = System.getProperty("db.user", DEFAULT_USER);
        String pass = System.getProperty("db.password", DEFAULT_PASSWORD);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL (com.mysql.cj.jdbc.Driver) não foi encontrado no classpath!", e);
        }
    }

    public static boolean testarConexao() {
        try (Connection con = getConexao()) {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            System.err.println("Erro ao conectar no MySQL: " + e.getMessage());
            return false;
        }
    }
}

