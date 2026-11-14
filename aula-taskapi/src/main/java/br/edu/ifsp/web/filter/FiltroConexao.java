package br.edu.ifsp.web.filter;

import br.edu.ifsp.web.dao.ConnectionFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebFilter("/tarefas/*") // intercepta toda requisição da TaskAPI
public class FiltroConexao implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        // IDA — antes de a lógica rodar:
        // a) recupere a ConnectionFactory do ServletContext
        //    (dica: req.getServletContext().getAttribute("connectionFactory"))
        ConnectionFactory fabrica = (ConnectionFactory)
                req.getServletContext().getAttribute("connectionFactory");

        Connection conexao;
        try {
            conexao = fabrica.getConnection();
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        try {
            // c) publique-a no request: req.setAttribute("conexao", conexao);
            req.setAttribute("conexao", conexao);

            chain.doFilter(req, resp); // deixa a requisição seguir para o Command

        } finally {
            // VOLTA — depois da resposta:
            // feche a conexão SEMPRE (use try/finally para não vazar conexão).
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("[FiltroConexao] Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
