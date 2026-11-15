package br.edu.ifsp.web.dao;

import br.edu.ifsp.web.model.Tarefa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    private final Connection conexao;

    // Agora o DAO RECEBE a conexão pronta (aberta pelo FiltroConexao).
    // Ele não abre nem fecha conexão — essa responsabilidade saiu daqui.
    public TarefaDAO(Connection conexao) {
        this.conexao = conexao;
    }

    // TODO: reimplemente os quatro métodos usando 'this.conexao'
    // (PreparedStatement sobre a conexão recebida — NÃO abra outra):
    public List<Tarefa> listar() throws SQLException {
        String sql = "SELECT id, titulo, descricao, concluida FROM tarefas ORDER BY id";
        List<Tarefa> tarefas = new ArrayList<>();

        // try-with-resources fecha o PreparedStatement e o ResultSet,
        // mas NÃO fecha a conexão (ela é do filtro).
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tarefas.add(new Tarefa(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getBoolean("concluida")));
            }
        }
        return tarefas;
    }

    public void inserir(Tarefa t) throws SQLException {
        String sql = "INSERT INTO tarefas (titulo, descricao, concluida) VALUES (?, ?, ?)";

        try (PreparedStatement ps =
                     conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTitulo());
            ps.setString(2, t.getDescricao());
            ps.setBoolean(3, t.isConcluida());
            ps.executeUpdate();

            // devolve o id gerado pelo AUTO_INCREMENT para o objeto
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    t.setId(chaves.getInt(1));
                }
            }
        }
    }

    public boolean atualizar(int id, Tarefa t) throws SQLException {
        String sql = "UPDATE tarefas SET titulo = ?, descricao = ?, concluida = ? WHERE id = ?";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, t.getTitulo());
            ps.setString(2, t.getDescricao());
            ps.setBoolean(3, t.isConcluida());
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;   // 0 linhas afetadas = id não existe
        }
    }

    public boolean deletar(int id) throws SQLException {
        String sql = "DELETE FROM tarefas WHERE id = ?";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
