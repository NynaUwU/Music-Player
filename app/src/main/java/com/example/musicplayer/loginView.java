package com.example.musicplayer;

import static android.view.View.INVISIBLE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.database.Usuario;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class loginView extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        Intent intent = getIntent();

        EditText nome = findViewById(R.id.JTFnome);
        EditText email = findViewById(R.id.JTFEmail);
        EditText senha = findViewById(R.id.JTFpassword);
        EditText senha2 = findViewById(R.id.JTFconfirmPassword);
        EditText IP = findViewById(R.id.JTserverIP);
        Button confirm = findViewById(R.id.JBlogin);

        if (intent.getBooleanExtra("login", true)) {
            senha2.setVisibility(INVISIBLE);
            nome.setVisibility(INVISIBLE);
        }


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Snome = String.valueOf(nome.getText());
                String Semail = String.valueOf(email.getText());
                String Ssenha = String.valueOf(senha.getText());
                String Ssenha2 = String.valueOf(senha2.getText());

                if (intent.getBooleanExtra("login", true)) {
                    if (!Semail.isEmpty() || !Ssenha.isEmpty()) {

                        MessageDigest md = null;
                        byte messageDigest[] = null;
                        try {
                            md = MessageDigest.getInstance("SHA-256");
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            messageDigest = md.digest(Ssenha.getBytes("UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        StringBuilder sb = new StringBuilder();
                        for (byte b : messageDigest) {
                            sb.append(String.format("%02x", 0xFF & b));
                        }
                        String senhaMax = sb.toString();

                        if (String.valueOf(IP.getText()).length() > 7) {
                            MainActivity.startAppDatabase(String.valueOf(IP.getText()));
                        }

                        Usuario result = null;
                        if (MainActivity.ccont != null) {
                            if (MainActivity.ccont.cliente != null) {
                                Usuario user = new Usuario(Semail, senhaMax);
                                result = MainActivity.ccont.usuarioLogin(user);
                            } else {
                                Toast.makeText(context, "Conexao perdida", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(context, "Conexao perdida", Toast.LENGTH_SHORT).show();
                        }

                        if (result != null) {
                            finish();
                        } else {
                            Toast.makeText(context, "Login falhou", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, "Por favor preenchaos campos", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (!Snome.isEmpty() || !Semail.isEmpty() || !Ssenha.isEmpty() || !Ssenha2.isEmpty()) {
                        if (Ssenha.equals(Ssenha2)) {
                            MessageDigest md = null;
                            byte messageDigest[] = null;
                            try {
                                md = MessageDigest.getInstance("SHA-256");
                            } catch (NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                messageDigest = md.digest(Ssenha.getBytes("UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                            StringBuilder sb = new StringBuilder();
                            for (byte b : messageDigest) {
                                sb.append(String.format("%02x", 0xFF & b));
                            }
                            String senhaMax = sb.toString();

                            if (IP.getText().toString().length() > 7) {
                                MainActivity.startAppDatabase(String.valueOf(IP.getText()));
                            }
                            boolean result = false;
                            if (MainActivity.ccont != null) {
                                if (MainActivity.ccont.cliente != null) {
                                    Usuario user = new Usuario(Snome, Semail, senhaMax);
                                    result = MainActivity.ccont.cadastrarUser(user);
                                } else {
                                    Toast.makeText(context, "Conexao perdida", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(context, "Conexao perdida", Toast.LENGTH_SHORT).show();
                            }

                            if (!result) {
                                finish();
                            } else {
                                Toast.makeText(context, "cadastro falhou", Toast.LENGTH_LONG).show();
                            }
                        }
                    }else {
                        Toast.makeText(context, "Por favor preenchaos campos", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

    }

}