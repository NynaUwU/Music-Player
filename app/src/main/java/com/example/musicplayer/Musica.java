package com.example.musicplayer;

import java.io.Serializable;
import java.sql.Timestamp;

public class Musica implements Serializable {
    private static final long serialVersionUID = 123456789L;

    // Campos do banco de dados
    private int id;
    private String nome;
    private String duracaoStr; // Para exibição como String (mantendo compatibilidade)
    private String artista;
    private int usuarioId;
    private int generoId;
    private String nomeGenero; // Nome do gênero (para facilitar exibição)
    private String arquivo; // Caminho do arquivo MP3
    private Timestamp dataUpload;

    // Campos adicionais da versão anterior
    private boolean capaAlbum; // URL ou caminho da imagem
    private boolean isMusic = true;

    // Construtor completo (para usar com ResultSet do banco)
    public Musica(int id, String nome, String duracao, String artista,
                  int usuarioId, int generoId, String nomeGenero,
                  String arquivo, Timestamp dataUpload) {
        this.id = id;
        this.nome = nome;

        this.duracaoStr = duracao;


        this.artista = artista;
        this.usuarioId = usuarioId;
        this.generoId = generoId;
        this.nomeGenero = nomeGenero;
        this.arquivo = arquivo;
        this.dataUpload = dataUpload;
        this.capaAlbum = false;
        this.isMusic = true;
    }

    // Construtor para cadastro (sem ID, será gerado pelo banco)
    public Musica(String nome, String duracao, String artista,
                  int usuarioId, int generoId, String arquivo) {
        this.nome = nome;

        this.duracaoStr = duracao;

        this.artista = artista;
        this.usuarioId = usuarioId;
        this.generoId = generoId;
        this.arquivo = arquivo;
        this.capaAlbum = false;
        this.isMusic = true;
    }

    // Construtor com String de duração (compatibilidade com código anterior)
    public Musica(String nome, String artista, String duracaoStr, String arquivo, boolean capaAlbum) {
        this.nome = nome;
        this.artista = artista;

        this.duracaoStr =duracaoStr;

        this.capaAlbum = capaAlbum;
        this.arquivo = arquivo;
        this.isMusic = true;
    }

    // Construtor simples (compatibilidade)
    public Musica(String nome, String artista, String duracaoStr, String arquivo) {
        this.nome = nome;
        this.artista = artista;

        this.duracaoStr =duracaoStr;

        this.capaAlbum = false;
        this.arquivo = arquivo;
        this.isMusic = true;
    }

    // Construtor para playlists (compatibilidade)
    public Musica(boolean isMusic, String nome, String arquivo, String quantMP3) {
        this.isMusic = isMusic;
        this.nome = nome;
        this.artista = arquivo;
        this.duracaoStr = quantMP3 + " Musicas";
        this.capaAlbum = false;
        this.arquivo = arquivo;
    }

    // Getters e Setters - Novos campos
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDuracaoStr() {
        return duracaoStr;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getGeneroId() {
        return generoId;
    }

    public void setGeneroId(int generoId) {
        this.generoId = generoId;
    }

    public String getNomeGenero() {
        return nomeGenero;
    }

    public void setNomeGenero(String nomeGenero) {
        this.nomeGenero = nomeGenero;
    }

    public Timestamp getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(Timestamp dataUpload) {
        this.dataUpload = dataUpload;
    }

    // Getters e Setters - Campos anteriores
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

    public boolean getCapaAlbum() {
        return capaAlbum;
    }

    public void setCapaAlbum(boolean capaAlbum) {
        this.capaAlbum = capaAlbum;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public void setMusic(boolean music) {
        isMusic = music;
    }

    @Override
    public String toString() {
        return "Musica{"
                + "id=" + id
                + ", nome='" + nome + '\''
                + ", artista='" + artista + '\''
                + ", duracao=" + duracaoStr
                + ", genero='" + nomeGenero + '\''
                + ", usuarioId=" + usuarioId
                + ", arquivo='" + arquivo + '\''
                + '}';
    }
}
