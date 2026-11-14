package br.edu.ifsp.web.command;

import br.edu.ifsp.web.command.Requisicao.DadosTarefa;
import br.edu.ifsp.web.service.TarefaService;
import br.edu.ifsp.web.servlet.RespostaJson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AtualizarTarefaCommand implements Command {

    private final TarefaService servico;

    public AtualizarTarefaCommand(TarefaService servico) {
        this.servico = servico;
    }

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Requisicao.id(req);
        DadosTarefa dados = Requisicao.dadosTarefa(req);
        RespostaJson.enviar(resp, HttpServletResponse.SC_OK,
                servico.atualizar(Requisicao.usuario(req), id, dados.titulo(), dados.descricao()));
    }
}
