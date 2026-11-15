package br.edu.ifsp.web.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// FORNECIDA — ajuste URL, usuário e senha conforme a sua instalação do MySQL.
public class ConnectionFactory {

    private static final String URL  = "jdbc:mysql://localhost:3306/taskdb";
    private static final String USER = "root";
    private static final String PASS = "";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao conectar no banco", e);
        }
    }
}
