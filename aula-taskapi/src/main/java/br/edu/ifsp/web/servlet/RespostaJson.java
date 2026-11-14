package br.edu.ifsp.web.servlet;

import br.edu.ifsp.web.service.AplicacaoException;
import br.edu.ifsp.web.service.CredenciaisInvalidasException;
import br.edu.ifsp.web.service.DadosInvalidosException;
import br.edu.ifsp.web.service.RecursoNaoEncontradoException;
import br.edu.ifsp.web.service.RegraDeNegocioException;
import com.google.gson.Gson;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public final class RespostaJson {

    public static final Gson GSON = new Gson();

    private RespostaJson() {}

    public static void enviar(ServletResponse resp, int status, Object corpo) throws IOException {
        HttpServletResponse http = (HttpServletResponse) resp;
        http.setStatus(status);
        http.setContentType("application/json");
        http.setCharacterEncoding("UTF-8");
        http.getWriter().print(GSON.toJson(corpo));
    }

    public static void erro(ServletResponse resp, int status, String mensagem) throws IOException {
        enviar(resp, status, Map.of("erro", mensagem));
    }

    public static void erro(ServletResponse resp, Exception e) throws IOException {
        if (e instanceof AplicacaoException ae && statusDe(ae) != 500) {
            erro(resp, statusDe(ae), ae.getMessage());
        } else {
            e.printStackTrace();
            erro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha interna.");
        }
    }

    private static int statusDe(AplicacaoException e) {
        if (e instanceof DadosInvalidosException)       return HttpServletResponse.SC_BAD_REQUEST;
        if (e instanceof CredenciaisInvalidasException) return HttpServletResponse.SC_UNAUTHORIZED;
        if (e instanceof RecursoNaoEncontradoException) return HttpServletResponse.SC_NOT_FOUND;
        if (e instanceof RegraDeNegocioException)       return HttpServletResponse.SC_CONFLICT;
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}
