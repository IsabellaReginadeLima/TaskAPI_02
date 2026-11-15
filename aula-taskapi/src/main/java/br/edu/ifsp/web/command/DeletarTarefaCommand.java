package br.edu.ifsp.web.command;

import br.edu.ifsp.web.dao.TarefaDAO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;

public class DeletarTarefaCommand implements Command {

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String path = req.getPathInfo();          // ex.: "/5"

        if (path == null || !path.matches("^/\\d+$")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalido.");  // 400
            return;
        }

        int id = Integer.parseInt(path.substring(1));
        Connection conexao = (Connection) req.getAttribute("conexao");
        boolean ok = new TarefaDAO(conexao).deletar(id);
        resp.setStatus(ok ? HttpServletResponse.SC_NO_CONTENT   // 204
                          : HttpServletResponse.SC_NOT_FOUND);  // 404
    }
}
