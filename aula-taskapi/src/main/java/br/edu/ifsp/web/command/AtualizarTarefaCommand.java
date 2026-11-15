package br.edu.ifsp.web.command;

import br.edu.ifsp.web.dao.TarefaDAO;
import br.edu.ifsp.web.model.Tarefa;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;

public class AtualizarTarefaCommand implements Command {

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String path = req.getPathInfo();          // ex.: "/5"

        // Atenção: em Java a barra invertida dobra -> "^/\\d+$"
        if (path == null || !path.matches("^/\\d+$")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalido.");  // 400
            return;
        }

        int id = Integer.parseInt(path.substring(1));
        Tarefa t = new Gson().fromJson(req.getReader(), Tarefa.class);

        if (t == null || t.getTitulo() == null || t.getTitulo().isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Titulo obrigatorio.");  // 400
            return;
        }

        Connection conexao = (Connection) req.getAttribute("conexao");
        boolean ok = new TarefaDAO(conexao).atualizar(id, t);
        resp.setStatus(ok ? HttpServletResponse.SC_OK          // 200
                          : HttpServletResponse.SC_NOT_FOUND); // 404
    }
}
