package com.example.musicplayer.Managers;

import android.content.Context;
import android.util.Log;

import com.example.musicplayer.Musica;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PlaylistManager {
    private static final String TAG = "PlaylistManager";
    private static final String FILE_EXTENSION = ".data";
    private static final String PLAYLISTS_FOLDER = "playlists";
    private static final int VERSION = 1; // Para compatibilidade futura

    private Context context;
    private File playlistsDir;

    public PlaylistManager(Context context) {
        this.context = context;
        this.playlistsDir = new File(context.getFilesDir(), PLAYLISTS_FOLDER);

        // Criar diretório se não existir
        if (!playlistsDir.exists()) {
            if (!playlistsDir.mkdirs()) {
                Log.e(TAG, "Erro ao criar diretório de playlists");
            }
        }
    }

    /**
     * Salva uma lista de objetos Musica em um arquivo binário .data
     * @param fileName Nome do arquivo (sem extensão)
     * @param musicList Lista de objetos Musica
     * @return true se salvou com sucesso, false caso contrário
     */
    public boolean savePlaylist(String fileName, List<Musica> musicList) {
        fileName= cleanFileName(fileName);
        if (fileName == null || fileName.trim().isEmpty()) {
            Log.e(TAG, "Nome do arquivo não pode ser vazio");
            return false;
        }

        if (musicList == null) {
            Log.e(TAG, "Lista de músicas não pode ser null");
            return false;
        }

        // Limpar nome do arquivo e adicionar extensão
        String cleanFileName = cleanFileName(fileName) + FILE_EXTENSION;
        File file = new File(playlistsDir, cleanFileName);

        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             DataOutputStream dos = new DataOutputStream(bos)) {

            // Escrever cabeçalho do arquivo
            dos.writeInt(VERSION); // Versão do formato
            dos.writeLong(System.currentTimeMillis()); // Timestamp de criação
            dos.writeInt(musicList.size()); // Quantidade de itens

            // Escrever cada música
            for (Musica musica : musicList) {
                if (musica != null) {
                    writeMusica(dos, musica);
                } else {
                    Log.w(TAG, "Música null encontrada na lista, pulando...");
                }
            }

            dos.flush();
            Log.i(TAG, "Playlist salva: " + cleanFileName + " (" + musicList.size() + " itens)");
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Erro ao salvar playlist: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carrega uma playlist do arquivo binário .data
     * @param fileName Nome do arquivo (com ou sem extensão)
     * @return Lista de objetos Musica, ou null se erro
     */
    public List<Musica> loadPlaylist(String fileName) {
        fileName= cleanFileName(fileName);
        if (fileName == null || fileName.trim().isEmpty()) {
            Log.e(TAG, "Nome do arquivo não pode ser vazio");
            return null;
        }

        // Adicionar extensão se necessário
        String fullFileName = fileName.endsWith(FILE_EXTENSION) ?
                fileName : fileName + FILE_EXTENSION;

        File file = new File(playlistsDir, fullFileName);

        if (!file.exists()) {
            Log.e(TAG, "Arquivo não encontrado: " + fullFileName);
            return null;
        }

        List<Musica> musicList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             DataInputStream dis = new DataInputStream(bis)) {

            // Ler cabeçalho
            int version = dis.readInt();
            long timestamp = dis.readLong();
            int itemCount = dis.readInt();

            Log.d(TAG, "Carregando playlist versão " + version + " com " + itemCount + " itens");

            // Ler cada música
            for (int i = 0; i < itemCount; i++) {
                Musica musica = readMusica(dis, version);
                if (musica != null) {
                    musicList.add(musica);
                } else {
                    Log.w(TAG, "Erro ao ler música no índice " + i);
                }
            }

            Log.i(TAG, "Playlist carregada: " + fullFileName + " (" + musicList.size() + " itens)");
            return musicList;

        } catch (IOException e) {
            Log.e(TAG, "Erro ao carregar playlist: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todos os arquivos de playlist salvos
     * @return Lista com nomes dos arquivos (sem extensão), ou lista vazia se nenhum
     */
    public List<String> getAllPlaylistNames() {

        List<String> playlistNames = new ArrayList<>();

        File[] files = playlistsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(FILE_EXTENSION.toLowerCase());
            }
        });

        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                // Remover extensão
                if (name.endsWith(FILE_EXTENSION)) {
                    name = name.substring(0, name.length() - FILE_EXTENSION.length());
                }
                playlistNames.add(name);
            }

            // Ordenar alfabeticamente
            Collections.sort(playlistNames, String.CASE_INSENSITIVE_ORDER);
        }

        Log.i(TAG, "Encontradas " + playlistNames.size() + " playlists");
        return playlistNames;
    }

    /**
     * Deleta uma playlist
     * @param fileName Nome do arquivo (com ou sem extensão)
     * @return true se deletou com sucesso, false caso contrário
     */
    public boolean deletePlaylist(String fileName) {
        fileName= cleanFileName(fileName);
        if (fileName == null || fileName.trim().isEmpty()) {
            Log.e(TAG, "Nome do arquivo não pode ser vazio");
            return false;
        }

        String fullFileName = fileName.endsWith(FILE_EXTENSION) ?
                fileName : fileName + FILE_EXTENSION;

        File file = new File(playlistsDir, fullFileName);

        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Log.i(TAG, "Playlist deletada: " + fullFileName);
            } else {
                Log.e(TAG, "Erro ao deletar playlist: " + fullFileName);
            }
            return deleted;
        } else {
            Log.e(TAG, "Arquivo não encontrado para deletar: " + fullFileName);
            return false;
        }
    }

    /**
     * Verifica se uma playlist existe
     * @param fileName Nome do arquivo (com ou sem extensão)
     * @return true se existe, false caso contrário
     */
    public boolean playlistExists(String fileName) {
        fileName= cleanFileName(fileName);

        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        String fullFileName = fileName.endsWith(FILE_EXTENSION) ?
                fileName : fileName + FILE_EXTENSION;

        File file = new File(playlistsDir, cleanFileName(fullFileName));
        return file.exists();
    }

    /**
     * Renomeia uma playlist
     * @param oldName Nome antigo (com ou sem extensão)
     * @param newName Novo nome (sem extensão)
     * @return true se renomeou com sucesso, false caso contrário
     */
    public boolean renamePlaylist(String oldName, String newName) {
        oldName= cleanFileName(oldName);

        if (oldName == null || oldName.trim().isEmpty() ||
                newName == null || newName.trim().isEmpty()) {
            Log.e(TAG, "Nomes não podem ser vazios");
            return false;
        }

        String oldFullName = oldName.endsWith(FILE_EXTENSION) ?
                oldName : oldName + FILE_EXTENSION;
        String newFullName = cleanFileName(newName) + FILE_EXTENSION;

        File oldFile = new File(playlistsDir, oldFullName);
        File newFile = new File(playlistsDir, newFullName);

        if (!oldFile.exists()) {
            Log.e(TAG, "Arquivo original não encontrado: " + oldFullName);
            return false;
        }

        if (newFile.exists()) {
            Log.e(TAG, "Arquivo de destino já existe: " + newFullName);
            return false;
        }

        boolean renamed = oldFile.renameTo(newFile);
        if (renamed) {
            Log.i(TAG, "Playlist renomeada: " + oldFullName + " -> " + newFullName);
        } else {
            Log.e(TAG, "Erro ao renomear playlist");
        }

        return renamed;
    }

    /**
     * Obtém informações sobre uma playlist sem carregar todos os dados
     * @param fileName Nome do arquivo (com ou sem extensão)
     * @return PlaylistInfo com dados da playlist, ou null se erro
     */
    public PlaylistInfo getPlaylistInfo(String fileName) {
        fileName= cleanFileName(fileName);
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }

        String fullFileName = fileName.endsWith(FILE_EXTENSION) ?
                fileName : fileName + FILE_EXTENSION;

        File file = new File(playlistsDir, fullFileName);

        if (!file.exists()) {
            return null;
        }

        // Ler apenas o cabeçalho para obter informações básicas
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             DataInputStream dis = new DataInputStream(bis)) {

            int version = dis.readInt();
            long timestamp = dis.readLong();
            int itemCount = dis.readInt();

            String displayName = fileName.endsWith(FILE_EXTENSION) ?
                    fileName.substring(0, fileName.length() - FILE_EXTENSION.length()) : fileName;

            // Para contar músicas vs pastas precisaríamos ler tudo, então vamos usar uma estimativa
            return new PlaylistInfo(displayName, itemCount, -1, -1, // -1 indica não contado
                    new Date(file.lastModified()), file.length(), new Date(timestamp));

        } catch (IOException e) {
            Log.e(TAG, "Erro ao ler informações da playlist: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtém informações detalhadas sobre uma playlist (carrega todos os dados)
     * @param fileName Nome do arquivo
     * @return PlaylistInfo detalhado
     */
    public PlaylistInfo getDetailedPlaylistInfo(String fileName) {
        fileName= cleanFileName(fileName);

        List<Musica> musicas = loadPlaylist(fileName);
        if (musicas == null) {
            return null;
        }

        String displayName = fileName.endsWith(FILE_EXTENSION) ?
                fileName.substring(0, fileName.length() - FILE_EXTENSION.length()) : fileName;

        // Contar músicas e pastas
        int musicCount = 0;
        int folderCount = 0;
        for (Musica musica : musicas) {
            if (musica.isMusic()) {
                musicCount++;
            } else {
                folderCount++;
            }
        }

        String fullFileName = fileName.endsWith(FILE_EXTENSION) ?
                fileName : fileName + FILE_EXTENSION;
        File file = new File(playlistsDir, fullFileName);

        return new PlaylistInfo(displayName, musicas.size(), musicCount, folderCount,
                new Date(file.lastModified()), file.length(), null);
    }

    /**
     * Adiciona uma música a uma playlist existente
     * @param fileName Nome da playlist
     * @param musica Objeto Musica para adicionar
     * @return true se adicionou com sucesso
     */
    public boolean addMusicToPlaylist(String fileName, Musica musica) {
        fileName= cleanFileName(fileName);
        List<Musica> playlist = loadPlaylist(fileName);
        if (playlist == null) {
            playlist = new ArrayList<>();
        }

        playlist.add(musica);
        return savePlaylist(fileName, playlist);
    }

    /**
     * Remove uma música de uma playlist pelo índice
     * @param fileName Nome da playlist
     * @param index Índice da música a remover
     * @return true se removeu com sucesso
     */
    public boolean removeMusicFromPlaylist(String fileName, int index) {
        fileName= cleanFileName(fileName);
        List<Musica> playlist = loadPlaylist(fileName);
        if (playlist == null || index < 0 || index >= playlist.size()) {
            return false;
        }

        playlist.remove(index);
        return savePlaylist(fileName, playlist);
    }

    /**
     * Escreve um objeto Musica no DataOutputStream
     */
    private void writeMusica(DataOutputStream dos, Musica musica) throws IOException {
        // isMusic
        dos.writeBoolean(musica.isMusic());

        // Nome
        writeString(dos, musica.getNome());

        // Artista
        writeString(dos, musica.getArtista());

        // Duração
        writeString(dos, musica.getDuracaoStr());

        // Arquivo
        writeString(dos, musica.getArquivo());

        // Capa do álbum
        boolean capaAlbum = musica.getCapaAlbum();
        dos.writeBoolean(capaAlbum);
    }

    /**
     * Lê um objeto Musica do DataInputStream
     */
    private Musica readMusica(DataInputStream dis, int version) throws IOException {
        // isMusic
        boolean isMusic = dis.readBoolean();

        // Nome
        String nome = readString(dis);

        // Artista
        String artista = readString(dis);

        // Duração
        String duracao = readString(dis);

        // Arquivo
        String arquivo = readString(dis);

        // Capa do álbum

        boolean hasBitmap = dis.readBoolean();

        // Criar objeto Musica usando o construtor apropriado
        if (isMusic) {
            return new Musica(nome, artista, duracao, arquivo, hasBitmap);
        } else {
            // Para pastas, extrair número de músicas da duração
            String quantMP3 = duracao.replace(" Musicas", "");
            return new Musica(false, nome, arquivo, quantMP3);
        }
    }

    /**
     * Escreve uma string no DataOutputStream (com suporte a null)
     */
    private void writeString(DataOutputStream dos, String str) throws IOException {
        if (str == null) {
            dos.writeBoolean(false);
        } else {
            dos.writeBoolean(true);
            dos.writeUTF(str);
        }
    }

    /**
     * Lê uma string do DataInputStream (com suporte a null)
     */
    private String readString(DataInputStream dis) throws IOException {
        boolean hasString = dis.readBoolean();
        return hasString ? dis.readUTF() : null;
    }

    /**
     * Remove caracteres inválidos do nome do arquivo
     */
    private String cleanFileName(String fileName) {
        if (fileName == null) {
            return null;
        } else {
            return fileName.trim()
                    .replaceAll("[\\\\/:*?\"<>|]", "_")
                    .replaceAll("\\s+", "_");
        }
    }
    /**
     * Classe para informações da playlist
     */
    public static class PlaylistInfo {
        public final String name;
        public final int totalItems;
        public final int musicCount;    // -1 se não contado
        public final int folderCount;   // -1 se não contado
        public final Date lastModified;
        public final long fileSize;
        public final Date createdDate;  // null se não disponível

        public PlaylistInfo(String name, int totalItems, int musicCount, int folderCount,
                            Date lastModified, long fileSize, Date createdDate) {
            this.name = name;
            this.totalItems = totalItems;
            this.musicCount = musicCount;
            this.folderCount = folderCount;
            this.lastModified = lastModified;
            this.fileSize = fileSize;
            this.createdDate = createdDate;
        }

        @Override
        public String toString() {
            if (musicCount >= 0 && folderCount >= 0) {
                return name + " (" + musicCount + " músicas, " + folderCount + " pastas)";
            } else {
                return name + " (" + totalItems + " itens)";
            }
        }

        public String getFormattedSize() {
            if (fileSize < 1024) return fileSize + " B";
            if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }
}

/* EXEMPLO DE USO:

// Inicializar o gerenciador
PlaylistManager manager = new PlaylistManager(context);

// Criar uma lista de músicas
List<Musica> musicas = new ArrayList<>();
musicas.add(new Musica("Bohemian Rhapsody", "Queen", "5:55", "/storage/Music/queen.mp3"));
musicas.add(new Musica(false, "Rock", "/storage/Music/Rock/", "25")); // Pasta com 25 músicas

// Salvar playlist (agora em formato binário .data)
boolean saved = manager.savePlaylist("Minha Playlist", musicas);

// Carregar playlist
List<Musica> loadedMusicas = manager.loadPlaylist("Minha Playlist");

// Listar todas as playlists
List<String> allPlaylists = manager.getAllPlaylistNames();

// Informações rápidas (só lê cabeçalho)
PlaylistInfo info = manager.getPlaylistInfo("Minha Playlist");

// Informações detalhadas (carrega tudo)
PlaylistInfo detailedInfo = manager.getDetailedPlaylistInfo("Minha Playlist");

// Tamanho do arquivo formatado
String size = info.getFormattedSize(); // Ex: "2.5 KB" ou "1.2 MB"

*/