package br.edu.ifsp.web.model;

public class Tarefa {

    private int id;
    private int usuarioId;
    private String titulo;
    private String descricao;
    private boolean concluida;

    public Tarefa(int usuarioId, String titulo, String descricao) {
        this.usuarioId = usuarioId;
        alterar(titulo, descricao);
    }
    
    public Tarefa(int id, int usuarioId, String titulo, String descricao) {
        this(usuarioId, titulo, descricao);
        this.id = id;
    }
    
    public void alterar(String titulo, String descricao) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Titulo obrigatorio.");
        }
        if (titulo.trim().length() > 120) {
            throw new IllegalArgumentException("Titulo deve ter no maximo 120 caracteres.");
        }
        if (descricao != null && descricao.length() > 500) {
            throw new IllegalArgumentException("Descricao deve ter no maximo 500 caracteres.");
        }
        this.titulo = titulo.trim();
        this.descricao = descricao;
    }

    public void concluir() {
        if (concluida) {
            throw new IllegalStateException("Tarefa ja concluida.");
        }
        concluida = true;
    }

    public boolean pertenceA(Usuario usuario) {
        return usuario != null && usuario.getId() == usuarioId;
    }

    // Getters e setters de cada campo
    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }

    public String getTitulo()           { return titulo; }
    public void setTitulo(String t)     { this.titulo = t; }

    public String getDescricao()        { return descricao; }
    public void setDescricao(String d)  { this.descricao = d; }

    public boolean isConcluida()        { return concluida; }
    public void setConcluida(boolean c) { this.concluida = c; }
    
    public int getUsuarioId()     { return usuarioId; }
}
