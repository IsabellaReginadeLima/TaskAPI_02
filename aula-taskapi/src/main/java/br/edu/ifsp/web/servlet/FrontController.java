package br.edu.ifsp.web.servlet;

import br.edu.ifsp.web.command.*;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/tarefas/*")            // captura /tarefas E /tarefas/{id}
public class FrontController extends HttpServlet {

    // mapa: método HTTP -> objeto Command
    private final Map<String, Command> comandos = new HashMap<>();

    // init() roda UMA vez, na subida do servlet.
    // Adicionar um endpoint = uma linha aqui. O controlador não muda.
    @Override
    public void init() {
        comandos.put("GET",    new ListarTarefasCommand());
        comandos.put("POST",   new CriarTarefaCommand());
        comandos.put("PUT",    new AtualizarTarefaCommand());
        comandos.put("DELETE", new DeletarTarefaCommand());
    }

    // service() é chamado pelo container para TODA requisição a /tarefas/*
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Command comando = comandos.get(req.getMethod());   // verbo -> comando
            if (comando != null) {
                comando.executar(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);  // 405
            }
        } catch (Exception e) {
            // Um único ponto trata a exceção de QUALQUER comando
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,    // 500
                           e.getMessage());
        }
    }
}
