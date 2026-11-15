package br.edu.ifsp.web.command;

import br.edu.ifsp.web.dao.TarefaDAO;
import br.edu.ifsp.web.model.Tarefa;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

public class ListarTarefasCommand implements Command {

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        // Dentro de cada Command (ex.: ListarTarefasCommand), a conexão agora vem do request:
        Connection conexao = (Connection) req.getAttribute("conexao");
        TarefaDAO dao = new TarefaDAO(conexao);
        List<Tarefa> tarefas = dao.listar();

        // ... resto igual (Gson etc.)
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(new Gson().toJson(tarefas));   // List -> JSON
        out.flush();
    }
}
