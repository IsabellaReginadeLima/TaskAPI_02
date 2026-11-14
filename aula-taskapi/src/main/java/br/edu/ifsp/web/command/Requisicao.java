package br.edu.ifsp.web.command;

import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.service.DadosInvalidosException;
import br.edu.ifsp.web.service.RecursoNaoEncontradoException;
import br.edu.ifsp.web.servlet.RespostaJson;
import com.google.gson.JsonParseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

final class Requisicao {

    record DadosTarefa(String titulo, String descricao) {}

    private Requisicao() {}

    static Usuario usuario(HttpServletRequest req) {
        HttpSession sessao = req.getSession(false);
        Usuario usuario = sessao == null ? null : (Usuario) sessao.getAttribute("usuario");
        if (usuario == null) {
            throw new IllegalStateException("Requisicao sem usuario chegou ao comando.");
        }
        return usuario;
    }

    static int id(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || !path.matches("^/\\d{1,9}$")) {
            throw new RecursoNaoEncontradoException("Recurso nao encontrado.");
        }
        return Integer.parseInt(path.substring(1));
    }

    static DadosTarefa dadosTarefa(HttpServletRequest req) throws IOException {
        try {
            DadosTarefa dados = RespostaJson.GSON.fromJson(req.getReader(), DadosTarefa.class);
            if (dados == null) {
                throw new DadosInvalidosException("Corpo da requisicao vazio.");
            }
            return dados;
        } catch (JsonParseException e) {
            throw new DadosInvalidosException("JSON invalido.");
        }
    }
}
