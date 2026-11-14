package br.edu.ifsp.web.service;

import br.edu.ifsp.web.dao.TarefaDAO;
import br.edu.ifsp.web.model.Tarefa;
import br.edu.ifsp.web.model.Usuario;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class TarefaServiceImpl implements TarefaService {

    private final Transacao transacao;

    public TarefaServiceImpl(Transacao transacao) {
        this.transacao = transacao;
    }

    @Override
    public List<Tarefa> listar(Usuario usuario) {
        int dono = idDe(usuario);
        return transacao.executar(c -> new TarefaDAO(c).listarPorUsuario(dono));
    }

    @Override
    public Tarefa buscar(Usuario usuario, int id) {
        int dono = idDe(usuario);
        return transacao.executar(c -> carregar(new TarefaDAO(c), id, dono, false));
    }

    @Override
    public Tarefa criar(Usuario usuario, String titulo, String descricao) {
        int dono = idDe(usuario);
        Tarefa tarefa;
        try {
            tarefa = new Tarefa(dono, titulo, descricao);
        } catch (IllegalArgumentException e) {
            throw new DadosInvalidosException(e.getMessage());
        }
        return transacao.executar(c -> {
            new TarefaDAO(c).inserir(tarefa);
            return tarefa;
        });
    }

    @Override
    public Tarefa atualizar(Usuario usuario, int id, String titulo, String descricao) {
        int dono = idDe(usuario);
        return transacao.executar(c -> {
            TarefaDAO dao = new TarefaDAO(c);
            Tarefa tarefa = carregar(dao, id, dono, true);
            try {
                tarefa.alterar(titulo, descricao);
            } catch (IllegalArgumentException e) {
                throw new DadosInvalidosException(e.getMessage());
            }
            dao.atualizar(tarefa);
            return tarefa;
        });
    }

    @Override
    public void concluir(Usuario usuario, int id) {
        int dono = idDe(usuario);
        transacao.executar(c -> {
            TarefaDAO dao = new TarefaDAO(c);
            Tarefa tarefa = carregar(dao, id, dono, true);
            try {
                tarefa.concluir();
            } catch (IllegalStateException e) {
                throw new RegraDeNegocioException(e.getMessage());
            }
            dao.atualizar(tarefa);
            return null;
        });
    }

    @Override
    public void excluir(Usuario usuario, int id) {
        int dono = idDe(usuario);
        transacao.executar(c -> {
            if (!new TarefaDAO(c).deletar(id, dono)) {
                throw naoEncontrada(id);
            }
            return null;
        });
    }

    private Tarefa carregar(TarefaDAO dao, int id, int dono, boolean bloquear) throws SQLException {
        return dao.buscar(id, dono, bloquear).orElseThrow(() -> naoEncontrada(id));
    }

    private static RecursoNaoEncontradoException naoEncontrada(int id) {
        return new RecursoNaoEncontradoException("Tarefa " + id + " nao encontrada.");
    }

    private static int idDe(Usuario usuario) {
        return Objects.requireNonNull(usuario, "usuario").getId();
    }
}
