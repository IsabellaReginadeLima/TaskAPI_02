package br.edu.ifsp.web.command;

import br.edu.ifsp.web.command.Requisicao.DadosTarefa;
import br.edu.ifsp.web.service.TarefaService;
import br.edu.ifsp.web.servlet.RespostaJson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CriarTarefaCommand implements Command {

    private final TarefaService servico;

    public CriarTarefaCommand(TarefaService servico) {
        this.servico = servico;
    }

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        DadosTarefa dados = Requisicao.dadosTarefa(req);
        RespostaJson.enviar(resp, HttpServletResponse.SC_CREATED,
                servico.criar(Requisicao.usuario(req), dados.titulo(), dados.descricao()));
    }
}
