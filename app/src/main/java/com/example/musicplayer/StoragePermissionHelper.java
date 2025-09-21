package com.example.musicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StoragePermissionHelper {
    
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private static final int REQUEST_CODE_MANAGE_STORAGE = 101;
    
    private Activity activity;
    private PermissionCallback callback;
    
    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }
    
    public StoragePermissionHelper(Activity activity) {
        this.activity = activity;
    }
    
    /**
     * Verifica e solicita permissões de armazenamento
     * @param callback Callback para resultado da permissão
     */
    public void requestStoragePermission(PermissionCallback callback) {
        this.callback = callback;
        
        if (hasStoragePermission()) {
            callback.onPermissionGranted();
            return;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            requestManageExternalStoragePermission();
        } else {
            // Android 10 e anteriores
            requestLegacyStoragePermission();
        }
    }
    
    /**
     * Verifica se já tem permissão de armazenamento
     * @return true se tem permissão
     */
    public boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ - verifica MANAGE_EXTERNAL_STORAGE
            return Environment.isExternalStorageManager();
        } else {
            // Android 10 e anteriores - verifica READ_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(activity, 
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    /**
     * Solicita permissão MANAGE_EXTERNAL_STORAGE para Android 11+
     */
    private void requestManageExternalStoragePermission() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_CODE_MANAGE_STORAGE);
        } catch (Exception e) {
            // Fallback para configurações gerais
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            activity.startActivityForResult(intent, REQUEST_CODE_MANAGE_STORAGE);
        }
    }
    
    /**
     * Solicita permissão READ_EXTERNAL_STORAGE para Android 10 e anteriores
     */
    private void requestLegacyStoragePermission() {
        ActivityCompat.requestPermissions(activity,
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            REQUEST_CODE_STORAGE_PERMISSION);
    }
    
    /**
     * Processa o resultado da solicitação de permissão
     * Chame este método no onRequestPermissionsResult da sua Activity
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (callback != null) callback.onPermissionGranted();
            } else {
                if (callback != null) callback.onPermissionDenied();
            }
        }
    }
    
    /**
     * Processa o resultado da Activity de configurações
     * Chame este método no onActivityResult da sua Activity
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_MANAGE_STORAGE) {
            if (hasStoragePermission()) {
                if (callback != null) callback.onPermissionGranted();
            } else {
                if (callback != null) callback.onPermissionDenied();
            }
        }
    }
    
    /**
     * Verifica se deve mostrar explicação da permissão
     * @return true se deve mostrar explicação
     */
    public boolean shouldShowRequestPermissionRationale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return false; // Para Android 11+ não há rationale
        }
        return ActivityCompat.shouldShowRequestPermissionRationale(activity,
            Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    
    /**
     * Método utilitário para uso rápido
     * Verifica permissão e solicita se necessário
     */
    public static void checkAndRequestPermission(Activity activity, PermissionCallback callback) {
        StoragePermissionHelper helper = new StoragePermissionHelper(activity);
        helper.requestStoragePermission(callback);
    }
}