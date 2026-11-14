package br.edu.ifsp.web.command;

import br.edu.ifsp.web.service.TarefaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DeletarTarefaCommand implements Command {

    private final TarefaService servico;

    public DeletarTarefaCommand(TarefaService servico) {
        this.servico = servico;
    }

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        servico.excluir(Requisicao.usuario(req), Requisicao.id(req));
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
