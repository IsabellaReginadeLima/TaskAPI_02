package br.edu.ifsp.web.command;

import br.edu.ifsp.web.service.TarefaService;
import br.edu.ifsp.web.servlet.RespostaJson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ListarTarefasCommand implements Command {

    private final TarefaService servico;

    public ListarTarefasCommand(TarefaService servico) {
        this.servico = servico;
    }

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        RespostaJson.enviar(resp, HttpServletResponse.SC_OK, servico.listar(Requisicao.usuario(req)));
    }
}
