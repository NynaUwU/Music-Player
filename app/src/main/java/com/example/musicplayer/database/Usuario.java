package com.example.musicplayer.database;

import java.io.Serializable;

public class Usuario implements Serializable {
    // O serialVersionUID é utilizado para controlar a versão da classe
    // e garantir que o servidor e cliente tenham a mesma versão.
    private static final long serialVersionUID = 123456789L;

    private int codUsuario;
    private String nome;
    private String email;
    private String senha;

    public int getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(int codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // Sempre teremos 3 construtores nas classes de domínio do sistema
    // construtor com todos os campos é utilizado para consultas (SELECTS) e edição (UPDATES)
    public Usuario(int codUsuario, String nome, String email, String senha) {
        this.codUsuario = codUsuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // construtor somente com o código é utilizado para apagar (DELETES)
    public Usuario(int codUsuario) {
        this.codUsuario = codUsuario;
    }

    // construtor sem o código é utilizado para inserir (INSERTS)
    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }


    // o toString é essencial para podermos realizar debug de código através da
    // utilização de prints do objeto
    @Override
    public String toString() {
        return "Usuario{" + "codUsuario=" + codUsuario + ", nome=" + nome + ", email=" + email + ", senha=" + senha + '}';
    }

}
