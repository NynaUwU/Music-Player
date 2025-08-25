package com.example.musicplayer;
public class Musica {
    private String nome;
    private String artista;
    private String duracao;
    private String capaAlbum; // URL ou caminho da imagem
    private String arquivo; // Caminho do arquivo de áudio

    // Construtor
    public Musica(String nome, String artista, String duracao, String capaAlbum, String arquivo) {
        this.nome = nome;
        this.artista = artista;
        this.duracao = duracao;
        this.capaAlbum = capaAlbum;
        this.arquivo = arquivo;
    }

    // Construtor simples
    public Musica(String nome, String artista, String duracao) {
        this.nome = nome;
        this.artista = artista;
        this.duracao = duracao;
        this.capaAlbum = "";
        this.arquivo = "";
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getCapaAlbum() {
        return capaAlbum;
    }

    public void setCapaAlbum(String capaAlbum) {
        this.capaAlbum = capaAlbum;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }
}