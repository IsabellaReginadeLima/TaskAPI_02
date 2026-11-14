package br.edu.ifsp.web.dao;

import br.edu.ifsp.web.model.Tarefa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TarefaDAO {

    private static final String COLUNAS = "id, usuario_id, titulo, descricao, concluida";

    private final Connection conexao;

    public TarefaDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public List<Tarefa> listarPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM tarefas WHERE usuario_id = ? ORDER BY id";
        List<Tarefa> tarefas = new ArrayList<>();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tarefas.add(montar(rs));
                }
            }
        }
        return tarefas;
    }

    public Optional<Tarefa> buscar(int id, int usuarioId, boolean bloquear) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM tarefas WHERE id = ? AND usuario_id = ?"
                + (bloquear ? " FOR UPDATE" : "");
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(montar(rs)) : Optional.empty();
            }
        }
    }

    public void inserir(Tarefa t) throws SQLException {
        String sql = "INSERT INTO tarefas (titulo, descricao, concluida, usuario_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTitulo());
            ps.setString(2, t.getDescricao());
            ps.setBoolean(3, t.isConcluida());
            ps.setInt(4, t.getUsuarioId());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    t.setId(chaves.getInt(1));
                }
            }
        }
    }

    public boolean atualizar(Tarefa t) throws SQLException {
        String sql = "UPDATE tarefas SET titulo = ?, descricao = ?, concluida = ? "
                + "WHERE id = ? AND usuario_id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, t.getTitulo());
            ps.setString(2, t.getDescricao());
            ps.setBoolean(3, t.isConcluida());
            ps.setInt(4, t.getId());
            ps.setInt(5, t.getUsuarioId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletar(int id, int usuarioId) throws SQLException {
        String sql = "DELETE FROM tarefas WHERE id = ? AND usuario_id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        }
    }

    private Tarefa montar(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa(
                rs.getInt("id"),
                rs.getInt("usuario_id"),
                rs.getString("titulo"),
                rs.getString("descricao"));
        if (rs.getBoolean("concluida")) {
            t.concluir();
        }
        return t;
    }
}
