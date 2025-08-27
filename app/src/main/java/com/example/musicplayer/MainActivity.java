package com.example.musicplayer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MusicaAdapter.OnMusicaClickListener {
    Context context;

    private RecyclerView recyclerView;
    private MusicaAdapter musicaAdapter;
    private List<Musica> listaMusicas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        context=this;

        recyclerView = findViewById(R.id.recycler_view_musicas);

        listaMusicas = new ArrayList<>();
        setupRecyclerView();
        setContentView(R.layout.activity_main);
        carregarMusicas();

    }


    private void setupRecyclerView() {
            listaMusicas = new ArrayList<>();
            musicaAdapter = new MusicaAdapter(this, listaMusicas);
            musicaAdapter.setOnMusicaClickListener(this);
            recyclerView.setAdapter(musicaAdapter);

    }

    private void carregarMusicas() {
        // Dados de exemplo - substitua pela sua fonte de dados
        listaMusicas.add(new Musica("Bohemian Rhapsody", "Queen", "5:55",null,null));
        listaMusicas.add(new Musica("Hotel California", "Eagles", "6:30",null,null  ));
        listaMusicas.add(new Musica("Stairway to Heaven", "Led Zeppelin", "8:02",null,null));
        listaMusicas.add(new Musica("Imagine", "John Lennon", "3:03",null,null));
        listaMusicas.add(new Musica("Sweet Child O' Mine", "Guns N' Roses", "5:03",null,null));
        listaMusicas.add(new Musica("Billie Jean", "Michael Jackson", "4:54",null,null));
        listaMusicas.add(new Musica("Like a Rolling Stone", "Bob Dylan", "6:13",null,null));
        listaMusicas.add(new Musica("Smells Like Teen Spirit", "Nirvana", "5:01",null,null));

        // Notificar o adapter sobre as mudanças
    }


    public void onMusicaClick(Musica musica, int position) {
        // Ação quando o card da música é clicado
        Toast.makeText(this, "Tocando: " + musica.getNome(), Toast.LENGTH_SHORT).show();

        // Aqui você pode implementar a lógica para tocar a música
        // Por exemplo: iniciar um MediaPlayer, abrir uma tela de reprodução, etc.
    }


    public void onOpcoesClick(Musica musica, int position, View view) {
        // Mostrar menu popup com opções
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_opcoes_musica, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.action_adicionar_playlist) {
                Toast.makeText(this, "Adicionar à playlist: " + musica.getNome(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_compartilhar) {
                Toast.makeText(this, "Compartilhar: " + musica.getNome(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_detalhes) {
                Toast.makeText(this, "Detalhes de: " + musica.getNome(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_remover) {
                removerMusica(position);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void removerMusica(int position) {
        if (position >= 0 && position < listaMusicas.size()) {
            String nomeMusica = listaMusicas.get(position).getNome();
            listaMusicas.remove(position);
            musicaAdapter.notifyItemRemoved(position);
            Toast.makeText(this, "Removido: " + nomeMusica, Toast.LENGTH_SHORT).show();
        }
    }
}