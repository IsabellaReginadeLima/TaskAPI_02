package br.edu.ifsp.web.model;

public class Tarefa {

    private int id;
    private String titulo;
    private String descricao;
    private boolean concluida;

    public Tarefa() {}   // construtor vazio: o Gson precisa dele

    public Tarefa(int id, String titulo, String descricao, boolean concluida) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.concluida = concluida;
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
}
