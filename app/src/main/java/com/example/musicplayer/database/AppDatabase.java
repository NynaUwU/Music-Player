package com.example.musicplayer.database;


import android.net.TrafficStats;
import android.os.StrictMode;
import android.util.Log;

import com.example.musicplayer.Musica;

import java.io.File;
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
    public Socket cliente;


    public AppDatabase(String IP) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); // PermitAll for testing, be cautious in production
            cliente=null;
        // Inside your network thread:

        new Thread(new Runnable() {
            @Override
            public void run() {
                TrafficStats.setThreadStatsTag((int) Thread.currentThread().getId());
                boolean isReachable = true;
//                try {
//                    isReachable = InetAddress.getByName(IP).isReachable(2500);
//                    Log.d("IP test", IP+" funciona: "+isReachable);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
                if (isReachable) {
                    try {
                        cliente = new Socket(IP, 12345);
                    } catch (RuntimeException | IOException e) {
                        throw new RuntimeException(e);

                    }
                }
                if (cliente!=null) {
                    try {
                        OutputStream outMP3 = cliente.getOutputStream();
                        InputStream inMP3 = cliente.getInputStream();
                        ObjectOutputStream out = new ObjectOutputStream(outMP3);
                        ObjectInputStream in = new ObjectInputStream(inMP3);
                        transfer(out, in, inMP3, outMP3);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

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
        if (cliente.isConnected()) {
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
        } else {
            return null;
        }
    }

    public boolean cadastrarMusica(Musica music) throws IOException, ClassNotFoundException {
        if (userLogado != null) {
            music.setUsuarioId(userLogado.getCodUsuario());
            try {
                out.writeObject("cadastrarMusica");
                in.readObject(); // lendo o "OK"
                out.writeObject(music);
                in.readObject(); // lendo o "OK"
                FileInputStream fileInputStream = new FileInputStream(music.getArquivo());
                File archiveFile = new File(music.getArquivo());
                out.writeObject(archiveFile.length());
                in.readObject(); // lendo o "OK"
                out.flush();
                long size = archiveFile.length();

                final int buffer_size = 4096;
                try {
                    byte[] bytes = new byte[buffer_size];
                    for (int count = 0, prog = 0; count != -1; ) {
                        count = fileInputStream.read(bytes);
                        if (count % 10 == 0) {
                            Log.d("upload", String.valueOf(((long) prog) * 100 / size));
                        }
                        if (count != -1) {
                            outMP3.write(bytes, 0, count);
                            prog = prog + count;

                        }
                    }
                    outMP3.flush();
                    out.flush();
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                in.readObject();
                in.readObject();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean cadastrarUser(Usuario User) {
        boolean result = false;
        if (cliente.isConnected()) {
            try {
                this.out.writeObject("CadastroUser");
                this.in.readObject(); // lendo o "OK"
                this.out.writeObject(User);
                result = (boolean) in.readObject();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return result;
        }

    }
}



