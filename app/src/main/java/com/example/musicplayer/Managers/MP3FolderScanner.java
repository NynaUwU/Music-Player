package com.example.musicplayer.Managers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MP3FolderScanner {

    private static final String TAG = "MP3FolderScanner";
    private Context context;
    private List<String> mp3Directories;

    // Pastas que devem ser evitadas na busca
    private static final Set<String> EXCLUDED_FOLDERS = new HashSet<>(Arrays.asList(
            "android_secure", ".android_secure", "system", "proc", "sys", "dev",
            "cache", ".cache", "tmp", ".tmp", "temp", ".temp",
            ".thumbnails", ".thumbdata", "lost+found", ".lost+found",
            "android", ".android", "data", ".data",
            ".recycle", "$recycle.bin", "recycler", ".trash",
            ".systemtrash", "system volume information",
            ".ads", ".spotify", ".whatsapp", ".telegram",
            "com.android", "com.google", "com.samsung"
    ));

    // Extensões que indicam arquivos de áudio MP3
    private static final Set<String> MP3_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".mp3", ".MP3"
    ));

    public MP3FolderScanner(Context context) {
        this.context = context;
        this.mp3Directories = new ArrayList<>();
    }

    /**
     * Inicia o scan procurando por pastas com arquivos MP3
     * @return Lista com os diretórios que contém arquivos MP3
     */
    public List<String> scanForMP3Folders() {
        mp3Directories.clear();

        try {
            // Verifica armazenamento externo principal
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File externalStorage = Environment.getExternalStorageDirectory();
                if (externalStorage != null && externalStorage.exists()) {
                    //Log.d(TAG, "Iniciando scan no armazenamento externo: " + externalStorage.getAbsolutePath());
                    scanDirectory(externalStorage);
                }
            }

            // Verifica pastas comuns de música
            scanCommonMusicDirectories();

        } catch (SecurityException e) {
            Log.e(TAG, "Erro de permissão ao acessar armazenamento: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Erro durante o scan: " + e.getMessage());
        }

        Log.d(TAG, "Scan concluído. Encontradas " + mp3Directories.size() + " pastas com MP3");
        return new ArrayList<>(mp3Directories);
    }

    /**
     * Escaneia recursivamente um diretório procurando por arquivos MP3
     * @param directory Diretório a ser escaneado
     */
    private void scanDirectory(File directory) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }

        // Verifica se a pasta deve ser excluída
        String folderName = directory.getName().toLowerCase();
        if (shouldExcludeFolder(folderName, directory.getAbsolutePath())) {
            return;
        }

        try {
            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                return;
            }

            boolean hasMp3Files = false;
            List<File> subdirectories = new ArrayList<>();

            // Primeiro passo: verifica se há arquivos MP3 nesta pasta
            for (File file : files) {
                if (file.isFile() && isMp3File(file)) {
                    hasMp3Files = true;
                } else if (file.isDirectory()) {
                    subdirectories.add(file);
                }
            }

            // Se encontrou MP3s nesta pasta, adiciona à lista
            if (hasMp3Files) {
                String dirPath = directory.getAbsolutePath();
                if (!mp3Directories.contains(dirPath)) {
                    mp3Directories.add(dirPath);
                    //Log.d(TAG, "Pasta com MP3 encontrada: " + dirPath);
                }
            }

            // Segundo passo: escaneia recursivamente os subdiretórios
            for (File subdir : subdirectories) {
                scanDirectory(subdir);
            }

        } catch (SecurityException e) {
            Log.w(TAG, "Acesso negado ao diretório: " + directory.getAbsolutePath());
        } catch (Exception e) {
            Log.w(TAG, "Erro ao escanear diretório " + directory.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    /**
     * Escaneia diretórios comuns onde músicas são armazenadas
     */
    private void scanCommonMusicDirectories() {
        String[] commonPaths = {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath(),
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music",
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/music",
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Audio",
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio",
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download",
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloads"
        };

        for (String path : commonPaths) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                scanDirectory(dir);
            }
        }
    }

    /**
     * Verifica se uma pasta deve ser excluída da busca
     * @param folderName Nome da pasta (em lowercase)
     * @param fullPath Caminho completo da pasta
     * @return true se a pasta deve ser excluída
     */
    private boolean shouldExcludeFolder(String folderName, String fullPath) {
        // Pastas que começam com ponto (ocultas)
        if (folderName.startsWith(".") && !folderName.equals(".")) {
            return true;
        }

        // Pastas da lista de exclusão
        if (EXCLUDED_FOLDERS.contains(folderName)) {
            return true;
        }

        // Pastas do sistema Android
        if (fullPath.contains("/Android/data") ||
                fullPath.contains("/android_secure") ||
                fullPath.contains("/.android_secure")) {
            return true;
        }

        // Pastas muito profundas (mais de 6 níveis)
        int depth = fullPath.split("/").length;
        if (depth > 8) {
            return true;
        }

        return false;
    }

    /**
     * Verifica se um arquivo é um MP3
     * @param file Arquivo a ser verificado
     * @return true se for um arquivo MP3
     */
    private boolean isMp3File(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }

        String fileName = file.getName().toLowerCase();
        for (String extension : MP3_EXTENSIONS) {
            if (fileName.endsWith(extension.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna a lista atual de diretórios encontrados
     * @return Lista de diretórios com arquivos MP3
     */
    public List<String> getMp3Directories() {
        return new ArrayList<>(mp3Directories);
    }

    /**
     * Limpa a lista de diretórios encontrados
     */
    public void clearResults() {
        mp3Directories.clear();
    }
}