package br.edu.ifsp.web.servlet;

import br.edu.ifsp.web.listener.SessaoListener;
import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.service.AutenticacaoService;
import br.edu.ifsp.web.service.DadosInvalidosException;
import com.google.gson.JsonParseException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

// Page Controller: /auth tem só três ações fixas
@WebServlet("/auth/*")
public class AuthController extends HttpServlet {

    private record Credenciais(String email, String senha) {}

    private AutenticacaoService servico;

    @Override
    public void init() {
        servico = (AutenticacaoService) getServletContext().getAttribute("autenticacaoService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            switch (acao(req)) {
                case "/login"  -> login(req, resp);
                case "/logout" -> logout(req, resp);
                default        -> naoEncontrado(resp);
            }
        } catch (Exception e) {
            RespostaJson.erro(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("/me".equals(acao(req))) {
            me(req, resp);
        } else {
            naoEncontrado(resp);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Credenciais cred;
        try {
            cred = RespostaJson.GSON.fromJson(req.getReader(), Credenciais.class);
        } catch (JsonParseException e) {
            throw new DadosInvalidosException("JSON invalido.");
        }
        if (cred == null) {
            throw new DadosInvalidosException("Informe email e senha.");
        }

        Usuario usuario = servico.autenticar(cred.email(), cred.senha());

        HttpSession sessao = req.getSession(false);
        if (sessao != null) {
            req.changeSessionId();
        } else {
            sessao = req.getSession(true);
        }
        sessao.setAttribute("usuario", usuario);

        RespostaJson.enviar(resp, HttpServletResponse.SC_OK, usuario);
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession sessao = req.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void me(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sessao = req.getSession(false);
        Usuario usuario = sessao == null ? null : (Usuario) sessao.getAttribute("usuario");
        if (usuario == null) {
            RespostaJson.erro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Usuario nao autenticado.");
            return;
        }
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("usuario", usuario);
        corpo.put("sessoesAtivas", SessaoListener.sessoesAtivas());
        RespostaJson.enviar(resp, HttpServletResponse.SC_OK, corpo);
    }

    private static String acao(HttpServletRequest req) {
        return req.getPathInfo() == null ? "" : req.getPathInfo();
    }

    private static void naoEncontrado(HttpServletResponse resp) throws IOException {
        RespostaJson.erro(resp, HttpServletResponse.SC_NOT_FOUND, "Recurso nao encontrado.");
    }
}
