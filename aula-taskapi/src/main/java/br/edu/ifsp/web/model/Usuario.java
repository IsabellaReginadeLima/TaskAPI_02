package br.edu.ifsp.web.model;

import java.io.Serializable;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String email;
    private transient String senha;

    public Usuario(int id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public int getId()           { return id; }
    public String getNome()      { return nome; }
    public String getEmail()     { return email; }
    public String getSenha() { return senha; }

    public Usuario semCredencial() {
        return new Usuario(id, nome, email, null);
    }
}
