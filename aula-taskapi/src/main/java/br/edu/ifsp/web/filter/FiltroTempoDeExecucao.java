package br.edu.ifsp.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter("/tarefas/*")
public class FiltroTempoDeExecucao implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        // IDA: marque o instante inicial (System.currentTimeMillis()).
        long inicio = System.currentTimeMillis();

        try {
            chain.doFilter(req, resp);
        } finally {
            // VOLTA: calcule a duração e imprima algo como:
            // "GET /tarefas levou (ms): 12"
            // Dica: a URI vem de ((HttpServletRequest) req).getRequestURI().
            long duracao = System.currentTimeMillis() - inicio;
            HttpServletRequest httpReq = (HttpServletRequest) req;
            System.out.println(httpReq.getMethod() + " " + httpReq.getRequestURI()
                    + " levou (ms): " + duracao);
        }
    }
}
