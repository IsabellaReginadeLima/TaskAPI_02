package br.edu.ifsp.web.filter;

import br.edu.ifsp.web.servlet.RespostaJson;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class FiltroAutenticacao implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpSession sessao = ((HttpServletRequest) req).getSession(false);
        if (sessao == null || sessao.getAttribute("usuario") == null) {
            RespostaJson.erro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Usuario nao autenticado.");
            return;
        }
        chain.doFilter(req, resp);
    }
}
