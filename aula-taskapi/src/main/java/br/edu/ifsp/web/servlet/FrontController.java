package br.edu.ifsp.web.servlet;

import br.edu.ifsp.web.command.*;
import br.edu.ifsp.web.service.TarefaService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/tarefas/*")
public class FrontController extends HttpServlet {

    private final Map<String, Command> comandos = new HashMap<>();

    @Override
    public void init() {
        TarefaService servico = (TarefaService) getServletContext().getAttribute("tarefaService");

        comandos.put("GET",          new ListarTarefasCommand(servico));
        comandos.put("POST",         new CriarTarefaCommand(servico));
        comandos.put("GET/{id}",     new BuscarTarefaCommand(servico));
        comandos.put("PUT/{id}",     new AtualizarTarefaCommand(servico));
        comandos.put("PATCH/{id}",   new ConcluirTarefaCommand(servico));
        comandos.put("DELETE/{id}",  new DeletarTarefaCommand(servico));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        boolean temId = path != null && !path.equals("/");
        Command comando = comandos.get(req.getMethod() + (temId ? "/{id}" : ""));

        try {
            if (comando == null) {
                RespostaJson.erro(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Metodo nao permitido.");
                return;
            }
            comando.executar(req, resp);
        } catch (Exception e) {
            RespostaJson.erro(resp, e);
        }
    }
}
