package com.example.musicplayer.Managers;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MP3Scanner {
    
    private static final String TAG = "Mp3Scanner";
    
    /**
     * Examina uma pasta e retorna todos os caminhos dos arquivos MP3 encontrados
     * @param folderPath Caminho da pasta para examinar
     * @param recursive Se true, busca recursivamente em subpastas
     * @return Lista com os caminhos dos arquivos MP3
     */
    public static List<String> scanMp3Files(String folderPath, boolean recursive) {
        List<String> mp3Files = new ArrayList<>();
        
        if (folderPath == null || folderPath.trim().isEmpty()) {
            Log.e(TAG, "Caminho da pasta é nulo ou vazio");
            return mp3Files;
        }
        
        File folder = new File(folderPath);
        
        if (!folder.exists()) {
            Log.e(TAG, "Pasta não existe: " + folderPath);
            return mp3Files;
        }
        
        if (!folder.isDirectory()) {
            Log.e(TAG, "O caminho não é uma pasta: " + folderPath);
            return mp3Files;
        }
        
        if (!folder.canRead()) {
            Log.e(TAG, "Sem permissão para ler a pasta: " + folderPath);
            return mp3Files;
        }
        
        scanFolder(folder, mp3Files, recursive);
        
        Log.i(TAG, "Encontrados " + mp3Files.size() + " arquivos MP3 em: " + folderPath);
        return mp3Files;
    }
    
    /**
     * Método recursivo para examinar pastas
     * @param folder Pasta atual
     * @param mp3Files Lista para armazenar os caminhos dos MP3
     * @param recursive Se deve buscar em subpastas
     */
    private static void scanFolder(File folder, List<String> mp3Files, boolean recursive) {
        File[] files = folder.listFiles();
        
        if (files == null) {
            Log.w(TAG, "Não foi possível listar arquivos em: " + folder.getAbsolutePath());
            return;
        }
        
        for (File file : files) {
            try {
                if (file.isFile() && isMp3File(file)) {
                    mp3Files.add(file.getAbsolutePath());
                    //Log.d(TAG, "MP3 encontrado: " + file.getAbsolutePath());
                } else if (file.isDirectory() && recursive) {
                    scanFolder(file, mp3Files, recursive);
                }
            } catch (SecurityException e) {
                Log.w(TAG, "Sem permissão para acessar: " + file.getAbsolutePath());
            }
        }
    }
    
    /**
     * Verifica se o arquivo é um MP3
     * @param file Arquivo para verificar
     * @return true se for MP3, false caso contrário
     */
    private static boolean isMp3File(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp3");
    }
    
    /**
     * Versão que retorna informações detalhadas dos MP3
     */
    public static class Mp3Info {
        private String filePath;
        private String fileName;
        private long fileSize;
        private long lastModified;
        
        public Mp3Info(String filePath, String fileName, long fileSize, long lastModified) {
            this.filePath = filePath;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.lastModified = lastModified;
        }
        
        // Getters
        public String getFilePath() { return filePath; }
        public String getFileName() { return fileName; }
        public long getFileSize() { return fileSize; }
        public long getLastModified() { return lastModified; }
        
        @Override
        public String toString() {
            return "Mp3Info{" +
                    "fileName='" + fileName + '\'' +
                    ", filePath='" + filePath + '\'' +
                    ", fileSize=" + fileSize + " bytes" +
                    ", lastModified=" + new java.util.Date(lastModified) +
                    '}';
        }
    }
    
    /**
     * Examina uma pasta e retorna informações detalhadas dos arquivos MP3
     * @param folderPath Caminho da pasta
     * @param recursive Se deve buscar recursivamente
     * @return Lista com informações detalhadas dos MP3
     */
    public static List<Mp3Info> scanMp3FilesDetailed(String folderPath, boolean recursive) {
        List<Mp3Info> mp3InfoList = new ArrayList<>();
        
        if (folderPath == null || folderPath.trim().isEmpty()) {
            Log.e(TAG, "Caminho da pasta é nulo ou vazio");
            return mp3InfoList;
        }
        
        File folder = new File(folderPath);
        
        if (!folder.exists() || !folder.isDirectory() || !folder.canRead()) {
            Log.e(TAG, "Problema com a pasta: " + folderPath);
            return mp3InfoList;
        }
        
        scanFolderDetailed(folder, mp3InfoList, recursive);
        
        Log.i(TAG, "Encontrados " + mp3InfoList.size() + " arquivos MP3 com detalhes em: " + folderPath);
        return mp3InfoList;
    }
    
    /**
     * Método recursivo para examinar pastas e coletar informações detalhadas
     */
    private static void scanFolderDetailed(File folder, List<Mp3Info> mp3InfoList, boolean recursive) {
        File[] files = folder.listFiles();
        
        if (files == null) return;
        
        for (File file : files) {
            try {
                if (file.isFile() && isMp3File(file)) {
                    Mp3Info info = new Mp3Info(
                        file.getAbsolutePath(),
                        file.getName(),
                        file.length(),
                        file.lastModified()
                    );
                    mp3InfoList.add(info);
                    //Log.d(TAG, "MP3 detalhado: " + info.toString());
                } else if (file.isDirectory() && recursive) {
                    scanFolderDetailed(file, mp3InfoList, recursive);
                }
            } catch (SecurityException e) {
                Log.w(TAG, "Sem permissão para acessar: " + file.getAbsolutePath());
            }
        }
    }
    
    /**
     * Método de conveniência para imprimir todos os MP3 encontrados
     * @param folderPath Caminho da pasta
     * @param recursive Se deve buscar recursivamente
     */
    public static void printMp3Files(String folderPath, boolean recursive) {
        List<String> mp3Files = scanMp3Files(folderPath, recursive);
        
        if (mp3Files.isEmpty()) {
            System.out.println("Nenhum arquivo MP3 encontrado.");
        }
    }
}