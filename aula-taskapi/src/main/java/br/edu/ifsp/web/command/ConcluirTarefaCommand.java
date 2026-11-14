package br.edu.ifsp.web.command;

import br.edu.ifsp.web.service.TarefaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ConcluirTarefaCommand implements Command {

    private final TarefaService servico;

    public ConcluirTarefaCommand(TarefaService servico) {
        this.servico = servico;
    }

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        servico.concluir(Requisicao.usuario(req), Requisicao.id(req));
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
