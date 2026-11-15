package br.edu.ifsp.web.command;

import br.edu.ifsp.web.dao.TarefaDAO;
import br.edu.ifsp.web.model.Tarefa;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.sql.Connection;

public class CriarTarefaCommand implements Command {

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        BufferedReader reader = req.getReader();
        Tarefa tarefa = new Gson().fromJson(reader, Tarefa.class);  // JSON -> objeto

        if (tarefa == null || tarefa.getTitulo() == null || tarefa.getTitulo().isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Titulo obrigatorio.");  // 400
            return;
        }

        Connection conexao = (Connection) req.getAttribute("conexao");
        new TarefaDAO(conexao).inserir(tarefa);

        // 201 + a tarefa criada (já com o id gerado pelo banco)
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(new Gson().toJson(tarefa));
    }
}
