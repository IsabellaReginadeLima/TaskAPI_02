package br.edu.ifsp.web.model;

public class Usuario {

    private int id;
    private String nome;
    private String email;
    private transient String senhaHash;

    public Usuario(int id, String nome, String email, String senhaHash) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
    }

    public int getId()           { return id; }
    public String getNome()      { return nome; }
    public String getEmail()     { return email; }
    public String getSenhaHash() { return senhaHash; }

    public Usuario semCredencial() {
        return new Usuario(id, nome, email, null);
    }
}