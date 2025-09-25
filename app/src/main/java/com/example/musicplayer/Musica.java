package com.example.musicplayer;

import android.graphics.Bitmap;

public class Musica {
    private String nome;
    private String artista;
    private String duracao;
    private Bitmap capaAlbum; // URL ou caminho da imagem
    private String arquivo; // Caminho do arquivo de áudio
    private boolean isMusic = true;

    // Construtor
    public Musica(String nome, String artista, String duracao, String arquivo, Bitmap capaAlbum) {
        this.nome = nome;
        this.artista = artista;
        this.duracao = duracao;
        this.capaAlbum = capaAlbum;
        this.arquivo = arquivo;
    }

    // Construtor simples
    public Musica(String nome, String artista, String duracao, String arquivo) {
        this.nome = nome;
        this.artista = artista;
        this.duracao = duracao;
        this.capaAlbum = null;
        this.arquivo = arquivo;
    }

    public Musica(boolean isMusic ,String nome,String arquivo, String QuantMP3) {
        this.isMusic = isMusic;
        this.nome = nome;
        this.artista = arquivo;
        this.duracao = QuantMP3+" Musicas";
        this.capaAlbum = null;
        this.arquivo = arquivo;
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

    public Bitmap getCapaAlbum() {
        return capaAlbum;
    }

    public void setCapaAlbum(Bitmap capaAlbum) {
        this.capaAlbum = capaAlbum;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public boolean isMusic() {return isMusic;}

    public void setMusic(boolean music) {isMusic = music;}
}