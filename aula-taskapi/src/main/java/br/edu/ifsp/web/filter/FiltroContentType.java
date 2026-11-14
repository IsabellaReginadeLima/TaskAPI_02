package br.edu.ifsp.web.filter;

import br.edu.ifsp.web.servlet.RespostaJson;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// BÔNUS: POST e PUT só passam se o corpo for declarado como JSON.
// Caso contrário, o filtro "fecha a porta" com 415 e a requisição
// nem chega ao FrontController.
public class FiltroContentType implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) resp;

        String metodo = httpReq.getMethod();
        boolean temCorpo = "POST".equals(metodo) || "PUT".equals(metodo);

        if (temCorpo) {
            String tipo = httpReq.getContentType();   // ex.: "application/json; charset=UTF-8"
            if (tipo == null || !tipo.toLowerCase().startsWith("application/json")) {
                RespostaJson.erro(httpResp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,   // 415
                        "Content-Type deve ser application/json");
                return;   // NÃO chama chain.doFilter: a requisição para aqui
            }
        }

        chain.doFilter(req, resp);
    }
}
