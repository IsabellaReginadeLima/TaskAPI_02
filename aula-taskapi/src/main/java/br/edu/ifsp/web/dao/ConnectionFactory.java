package br.edu.ifsp.web.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private final String url  = config("TASKDB_URL", "jdbc:mysql://localhost:3307/taskdb");
    private final String user = config("TASKDB_USER", "root");
    private final String pass = config("TASKDB_PASS", "");

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    private static String config(String chave, String padrao) {
        String valor = System.getProperty(chave, System.getenv(chave));
        return valor != null ? valor : padrao;
    }
}
