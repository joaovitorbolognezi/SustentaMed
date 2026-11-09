package com.sustentamed.app;

import com.sustentamed.factory.ConnectionFactory;
import java.sql.Connection;
import java.sql.SQLException;

public class TestaConexao {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  SustentaMed - Teste de Conexão com o MySQL");
        System.out.println("==================================================");

        try (Connection con = ConnectionFactory.getConexao()) {
            if (con != null && !con.isClosed()) {
                System.out.println(">> SUCESSO: Conexão estabelecida com o banco 'sistema_saude'!");
                System.out.println(">> Driver: " + con.getMetaData().getDriverName());
                System.out.println(">> Versão do MySQL: " + con.getMetaData().getDatabaseProductVersion());
            }
        } catch (SQLException e) {
            System.err.println(">> FALHA: Não foi possível conectar ao banco de dados.");
            System.err.println(">> Detalhe: " + e.getMessage());
            System.err.println("\nDicas:");
            System.err.println("1. O serviço do MySQL está rodando na porta 3306?");
            System.err.println("2. Você executou o arquivo 'schema.sql' para criar o banco 'sistema_saude'?");
            System.err.println("3. Se o seu usuário 'root' tiver senha, configure-a em 'ConnectionFactory.java'.");
        }
    }
}

