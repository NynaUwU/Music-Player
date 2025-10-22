package com.example.musicplayer.database;


import android.net.TrafficStats;
import android.os.StrictMode;

import com.example.musicplayer.Musica;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class AppDatabase {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private OutputStream outMP3;
    private InputStream inMP3;
    private Usuario userLogado;


    public AppDatabase() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); // PermitAll for testing, be cautious in production

        // Inside your network thread:

        new Thread(new Runnable() {
            @Override
            public void run() {
                TrafficStats.setThreadStatsTag((int) Thread.currentThread().getId());

                try {
                    Socket cliente = new Socket("192.168.20.20", 12345);
                    OutputStream outMP3 = cliente.getOutputStream();
                    InputStream inMP3 = cliente.getInputStream();
                    ObjectOutputStream out = new ObjectOutputStream(outMP3);
                    ObjectInputStream in = new ObjectInputStream(inMP3);
                    transfer(out, in, inMP3, outMP3);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    private void transfer(ObjectOutputStream out, ObjectInputStream in, InputStream inMP3, OutputStream outMP3) {
        this.out = out;
        this.in = in;
        this.inMP3 = inMP3;
        this.outMP3 = outMP3;
    }

    public Usuario getUserLogado() {
        return userLogado;
    }

    public void setUserLogado(Usuario userLogado) {
        this.userLogado = userLogado;
    }

    // TODO: Métodos que se comunicam com o servidor
    public Usuario usuarioLogin(Usuario user) {

        try {

            this.out.writeObject("UsuarioLogin");
            this.in.readObject(); // lendo o "OK"
            this.out.writeObject(user);
            this.userLogado = (Usuario) in.readObject();
            return this.userLogado;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean cadastrarMusica(Musica music) throws IOException, ClassNotFoundException {
        try {
            Usuario user = new Usuario("adm@adm.com", "1234");
            userLogado = usuarioLogin(user);
            music.setUsuarioId(userLogado.getCodUsuario());

            out.writeObject("cadastrarMusica");
            in.readObject(); // lendo o "OK"
            out.writeObject(music);
            in.readObject(); // lendo o "OK"
            FileInputStream fileInputStream = new FileInputStream(music.getArquivo());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outMP3.write(buffer, 0, bytesRead);
            }
            out.flush();
            //in.readObject();

            return true;
        } catch (Exception e ){
            e.printStackTrace();
            return false;
        }

    }
}

