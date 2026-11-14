package br.edu.ifsp.web.service;

import br.edu.ifsp.web.dao.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

// Abre uma conexão por operação de serviço: commit no sucesso, rollback em qualquer falha.
public class Transacao {

    @FunctionalInterface
    public interface Trabalho<T> {
        T executar(Connection conexao) throws SQLException;
    }

    private final ConnectionFactory fabrica;

    public Transacao(ConnectionFactory fabrica) {
        this.fabrica = fabrica;
    }

    public <T> T executar(Trabalho<T> trabalho) {
        try (Connection conexao = fabrica.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                T resultado = trabalho.executar(conexao);
                conexao.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new FalhaInternaException("Falha ao acessar o banco de dados.", e);
        }
    }
}
