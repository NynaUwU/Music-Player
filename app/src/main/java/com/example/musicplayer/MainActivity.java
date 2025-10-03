package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Managers.MP3FolderScanner;
import com.example.musicplayer.Managers.MP3Scanner;
import com.example.musicplayer.Managers.PlaylistManager;
import com.example.musicplayer.database.AppDatabase;
import com.example.musicplayer.database.Usuario;
import com.google.android.material.navigation.NavigationView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MusicaAdapter.OnMusicaClickListener {
    private static AppDatabase ccont;
    Context context;
    private StoragePermissionHelper permissionHelper;
    private ImageButton sideMenuButton;
    private ConstraintLayout FLbotton;
    private NavigationView sideMenu;
    private RecyclerView recyclerView;
    private MediaMetadataRetriever mp3Info = new MediaMetadataRetriever();
    private MusicaAdapter musicaAdapter;
    private PlaylistManager playlistManager;
    private List<Musica> listaMusicas;
    private List<Musica> listaMusicasOnline;
    private List<Musica> listaPastasMusica;
    public static Musica PlayingNow;
    private String WhereAreWe = null;
    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        intent = new Intent(context, musicView.class);

        //Declarar botoes
        sideMenuButton = findViewById((R.id.sideMenuButton));
        FLbotton = findViewById(R.id.FLbotton);
        sideMenu = findViewById(R.id.navBarLateral);
        sideMenu.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        permissionHelper = new StoragePermissionHelper(this);
        playlistManager = new PlaylistManager(context);


        // Solicita permissão para armazenamento
        permissionHelper.requestStoragePermission(new StoragePermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                // Permissão concedida - pode usar o scanner
                //Toast.makeText(MainActivity.this, "Permissão concedida!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied() {
                // Permissão negada
                Toast.makeText(MainActivity.this, "Permissão negada. Não é possível acessar arquivos.", Toast.LENGTH_LONG).show();
            }
        });




        //Banco de dados
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket cliente = new Socket("192.168.1.20", 12345);
                    ObjectOutputStream out = new ObjectOutputStream(cliente.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(cliente.getInputStream());
                    ccont = new AppDatabase(out, in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            Usuario user = new Usuario("abc", "123");
            ccont.usuarioLogin(user);

        } catch (Exception e) {
            e.printStackTrace();
        }


        //Musicas
        recyclerView = findViewById(R.id.recycler_view_musicas);
        listaMusicas = new ArrayList<>();
        listaPastasMusica = new ArrayList<>();

        setupRecyclerView();
        carregarMusicas(null, false);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(NavigationView.DRAWING_CACHE_QUALITY_HIGH);

        sideMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sideMenu.getVisibility() == NavigationView.VISIBLE) {
                    sideMenu.setVisibility(NavigationView.GONE);
                } else {
                    sideMenu.setVisibility(NavigationView.VISIBLE);
                }

            }
        });

        FLbotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });



    }

    //Recycler view stuff --->
    private void setupRecyclerView() {
        listaMusicas = new ArrayList<>();
        musicaAdapter = new MusicaAdapter(this, listaMusicas);
        musicaAdapter.setOnMusicaClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicaAdapter);
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.RefreshButton) {
            // Handle home item click
            carregarMusicas(WhereAreWe,true);
        } else if (id == R.id.foldersButton) {
            // Handle settings item click
            WhereAreWe=null;
            carregarMusicas(WhereAreWe,false);
        }


        sideMenu.setVisibility(View.GONE); // Close the drawer after selection
        return true;
    }

    private void carregarMusicas(String folderToLoad, boolean refresh) {
        // Dados

        List<String> mp3Folders = new ArrayList<>();

        if (folderToLoad == null) {
            if (playlistManager.playlistExists("Folders") && !refresh) {
                listaPastasMusica.clear();
                listaPastasMusica = playlistManager.loadPlaylist("Folders");
                listaMusicas.clear();
                listaMusicas = listaPastasMusica;

            } else {
                MP3FolderScanner scanner = new MP3FolderScanner(context);
                mp3Folders = scanner.scanForMP3Folders();

                // Os diretórios ficam disponíveis na lista
                listaPastasMusica.clear();


                for (String folder : mp3Folders) {
                    Log.d("MP3Folders", "Pasta encontrada: " + folder);
                    List<String> temp = MP3Scanner.scanMp3Files(folder, false);
                    Musica tempM = new Musica(false, folder.substring(folder.lastIndexOf('/') + 1), folder, String.valueOf(temp.size()));
                    listaMusicas.add(tempM);
                    listaPastasMusica.add(tempM);
                }
                playlistManager.savePlaylist("Folders", listaPastasMusica);
            }
        } else {
            listarMP3(folderToLoad, refresh);
        }

        musicaAdapter.updateList(listaMusicas);
    }

    private void listarMP3(String folderToLoad, boolean refresh) {

        if (playlistManager.playlistExists(folderToLoad.replaceAll("/","")) && !refresh) {
            listaMusicas.clear();
            listaMusicas = playlistManager.loadPlaylist(folderToLoad.replaceAll("/",""));

        } else {
            List<String> mp3Folders = new ArrayList<>();
            listaMusicas.clear();

            mp3Folders = MP3Scanner.scanMp3Files(folderToLoad, false);

            //Carregar MP3's


            for (String folder : mp3Folders) {
                Log.d("MP3 Scanner", "MP3's encontrado: " + folder);
                mp3Info.setDataSource(folder);

                //Organizar informações

                //get title
                String title;
                if (mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) == null) {
                    title = folder.substring(folder.lastIndexOf('/') + 1);
                } else {
                    title = mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                }

                //GET SONG IMAGE
                byte[] albumArtBytes = mp3Info.getEmbeddedPicture();

                // author/artista
                String author;

                //Se n ouver author, usa o compositor
                if (mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR) == null) {
                    if (mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER) == null) {
                        if (mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) == null) {
                            if (mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) == null) {
                                author = "Desconhecido";
                            } else {
                                author = mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
                            }
                        } else {
                            author = mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        }
                    } else {
                        author = mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
                    }
                } else {
                    author = mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
                }

                //formatação do tempo
                long milliseconds = Long.parseLong(mp3Info.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                long minutos = (milliseconds / 1000) / 60;
                long seconds = (milliseconds / 1000) % 60;
                String duracao = minutos + ":";
                if (String.valueOf(seconds).length() == 1) {
                    duracao = duracao + "0" + seconds;
                } else {
                    duracao += seconds;
                }

                if (albumArtBytes != null) {
                    Bitmap albumArt = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
                    //add Song
                    Musica tempM = new Musica(
                            title,
                            author,
                            duracao,
                            folder,
                            albumArt
                    );
                    listaMusicas.add(tempM);

                } else {
                    //add song
                    Musica tempM = new Musica(
                            title,
                            author,
                            duracao,
                            folder
                    );

                    listaMusicas.add(tempM);
                }

            }
            playlistManager.savePlaylist(folderToLoad.replaceAll("/",""),listaMusicas);
        }
    }


    public void onMusicaClick(Musica musica, int position) {
        // Ação quando o card da música é clicado


        if (musica.isMusic()) {
            Toast.makeText(this, "Tocando: " + musica.getNome(), Toast.LENGTH_SHORT).show();

            PlayingNow = musica;
        } else {
            WhereAreWe = musica.getArquivo();
            carregarMusicas(musica.getArquivo(), false);
            Toast.makeText(this, "indo para: " + musica.getNome(), Toast.LENGTH_SHORT).show();
        }

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