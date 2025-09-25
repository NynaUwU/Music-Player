package com.example.musicplayer.database;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AppDatabase {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Usuario userLogado;


    public AppDatabase(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
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
            out.writeObject("UsuarioLogin");
            in.readObject(); // lendo o "OK"
            out.writeObject(user);
            Usuario userLogado = (Usuario) in.readObject();
            return userLogado;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

