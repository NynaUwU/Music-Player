package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Managers.MP3FolderScanner;
import com.example.musicplayer.Managers.MP3Scanner;
import com.example.musicplayer.Managers.PlaylistManager;
import com.example.musicplayer.Managers.playerManager;
import com.example.musicplayer.database.AppDatabase;
import com.example.musicplayer.database.Usuario;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlinx.coroutines.Delay;


public class MainActivity extends AppCompatActivity implements MusicaAdapter.OnMusicaClickListener {
    public static Musica PlayingNow;
    public static AppDatabase ccont;
    public boolean isRunning = false;
    Context context;
    Intent intent;
    Intent intentcadastro;
    private Usuario userLogado;
    private NavigationView sideMenu;
    private RecyclerView recyclerView;
    private SeekBar seekBar;
    private Thread updateThread;
    private ImageButton playButton;

    //adapters
    private MediaMetadataRetriever mp3Info = new MediaMetadataRetriever();
    public MusicaAdapter musicaAdapter;
    private PlaylistManager playlistManager;
    //listas
    private List<Musica> listaMusicas;
    private List<Musica> listaMusicasOnline;
    private List<Musica> listaPastasMusica;
    private List<Musica> listaPlayingNow;
    public static playerManager playerManager;
    private Thread audioThread;
    private String WhereAreWe = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        intent = new Intent(context, musicView.class);
        intentcadastro = new Intent(context, loginView.class);

        //Declarar botoes
        ImageButton sideMenuButton = findViewById((R.id.sideMenuButton));
        ConstraintLayout FLbotton = findViewById(R.id.FLbotton);
        sideMenu = findViewById(R.id.navBarLateral);
        sideMenu.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        ImageButton forwardButton = findViewById(R.id.forwardButton);
        sideMenuButton = findViewById(R.id.sideMenuButton);
        playButton = findViewById(R.id.playButton);
        ImageButton backButton = findViewById(R.id.backButton);
        TextView musicPlayName = findViewById(R.id.musicPlayName);
        // musicView
        ImageView musicView = findViewById(R.id.musicView);
        seekBar = findViewById(R.id.seekBar);

        StoragePermissionHelper permissionHelper = new StoragePermissionHelper(this);
        playlistManager = new PlaylistManager(context);


        //limpar arquivos salvos

        //List<String> yep = playlistManager.getAllPlaylistNames();
        //for (String ye : yep){playlistManager.deletePlaylist(ye);}


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


        //TODO:Banco de dados
        ccont = new AppDatabase();


        //Musicas
        recyclerView = findViewById(R.id.recycler_view_musicas);
        listaMusicas = new ArrayList<>();
        listaPastasMusica = new ArrayList<>();
        setupRecyclerView();
        carregarMusicas(null, false);
        recyclerView.setItemViewCacheSize(80);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(NavigationView.DRAWING_CACHE_QUALITY_LOW);

        //TODO music manager


        if (audioThread == null || !audioThread.isAlive()) {
            playerManager = new playerManager(context, this);
            audioThread = new Thread(playerManager);
            audioThread.start();
        }
        //TODO barra
        setupSeekBar();


//        try {
//            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(PlayingNow.getArquivo()));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

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

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopUpdateThread();
                playerManager.nextMediaPlayer();
                startUpdateThread();
                PlayingNow = playerManager.music;
                updateScreenComponents();
            }
        });

        playButton.setOnClickListener(v -> {
            // Play/Pause
            if (playerManager != null) {
                playerManager.playPausePlayback();
                if (playerManager.isPlaying()) {
                    playButton.setImageResource(R.drawable.pause_circle);
                } else {
                    playButton.setImageResource(R.drawable.play_circle);
                }
            }
        });

        backButton.setOnClickListener(v -> {
            // Faixa anterior
            // TODO rewind button
        });
        musicPlayName.setOnClickListener(v -> {
            // Você pode abrir um dialog, playlist, etc.
            startActivity(intent);

        });
        musicView.setOnClickListener(v -> {
            startActivity(intent);
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
            carregarMusicas(WhereAreWe, true);
        } else if (id == R.id.foldersButton) {
            // Handle settings item click
            WhereAreWe = null;
            carregarMusicas(WhereAreWe, false);
        } else if (id == R.id.Registerbutt) {
            intentcadastro.putExtra("login", false);
            startActivity(intentcadastro);
        } else if (id == R.id.Loginbutt) {
            intentcadastro.putExtra("login", true);
            startActivity(intentcadastro);
        }


        sideMenu.setVisibility(View.GONE); // Close the drawer after selection
        return true;
    }

    public void updateScreenComponents() {
        ImageView AlbumCoverPlaying2 = findViewById(R.id.musicView);
        TextView MusicPlaying2 = findViewById(R.id.musicPlayName);
        if (PlayingNow != null) {
            if (PlayingNow.getCapaAlbum()) {
                mp3Info.setDataSource(PlayingNow.getArquivo());

                byte[] albumArtBytes = mp3Info.getEmbeddedPicture();
                Bitmap albumArt = null;
                if (albumArtBytes != null) {
                    albumArt = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
                    AlbumCoverPlaying2.setImageBitmap(albumArt);
                }
            }

            //AlbumCoverPlaying2.setImageBitmap(MainActivity.PlayingNow.getCapaAlbum());
            MusicPlaying2.setText(PlayingNow.getNome());
            if (playerManager.isPlaying()) {
                playButton.setImageResource(R.drawable.pause_circle);
            } else {
                playButton.setImageResource(R.drawable.pause_circle);
            }
        }

    }

    public void startUpdateThread() {
        isRunning = true;
        updateThread = new Thread(() -> {
            int time = 100;
            try {
                time = playerManager.getTotalTime();
            } catch (Exception ignored) {
            }
            while (isRunning) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (seekBar.getMax() != time) {
                    seekBar.setMax(time);
                }
                seekBar.setProgress(playerManager.getProgress());
            }
            isRunning = false;
        });
        updateThread.start();

        if (playerManager.isPlaying()) {
            playButton.setImageResource(R.drawable.pause_circle);
        } else {
            playButton.setImageResource(R.drawable.pause_circle);
        }
    }

    public void stopUpdateThread() {
        isRunning = false;

        if (updateThread != null) {
            try {
                updateThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (playerManager.isPlaying()) {
            playButton.setImageResource(R.drawable.pause_circle);
        } else {
            playButton.setImageResource(R.drawable.pause_circle);
        }
    }

    public void setupSeekBar() {
        seekBar.setMax(playerManager.getTotalTime());
        seekBar.setProgress(0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Atualizar tempo atual baseado na posição da seekbar
                    playerManager.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pausar atualização quando o usuário tocá-la
                stopUpdateThread();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Retomar atualização quando soltar
                startUpdateThread();
            }
        });
    }

    private void carregarMusicas(String folderToLoad, boolean refresh) {
        // Dados



        if (folderToLoad == null) {
            if (playlistManager.playlistExists("Folders") && !refresh) {

                listaPastasMusica.clear();
                listaPastasMusica = playlistManager.loadPlaylist("Folders");
                listaMusicas.clear();
                listaMusicas = listaPastasMusica;

            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MP3FolderScanner scanner = new MP3FolderScanner(context);
                        List<String> mp3Folders = new ArrayList<>();
                        mp3Folders = scanner.scanForMP3Folders();

                        // Os diretórios ficam disponíveis na lista
                        listaPastasMusica.clear();


                        for (String folder : mp3Folders) {
                            Log.d("MP3Folders", "Pasta encontrada: " + folder);
                            List<String> temp = MP3Scanner.scanMp3Files(folder, false);
                            Musica tempM = new Musica(false, folder.substring(folder.lastIndexOf('/') + 1), folder, String.valueOf(temp.size()));


                            if (!listaPastasMusica.contains(tempM)) {
                                listaPastasMusica.add(tempM);
                                listarMP3(folder, true);
                            }
                        }
                        listaMusicas = listaPastasMusica;
                        musicaAdapter.updateList(listaMusicas);
                        playlistManager.deletePlaylist("Folders");
                        playlistManager.savePlaylist("Folders", listaPastasMusica);

                    }

                }).start();
            }
        } else {
            listarMP3(folderToLoad, refresh);
        }

        musicaAdapter.updateList(listaMusicas);

    }

    private void listarMP3(String folderToLoad, boolean refresh) {

        if (playlistManager.playlistExists(folderToLoad.replaceAll("/", "")) && !refresh) {
            listaMusicas.clear();
            listaMusicas = playlistManager.loadPlaylist(folderToLoad.replaceAll("/", ""));

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

                    //add Song
                    Musica tempM = new Musica(
                            title,
                            author,
                            duracao,
                            folder,
                            true
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
            playlistManager.savePlaylist(folderToLoad.replaceAll("/", ""), listaMusicas);
        }
    }


    public void onMusicaClick(Musica musica, int position) {
        // Ação quando o card da música é clicado


        if (musica.isMusic()) {
            Toast.makeText(this, "Tocando: " + musica.getNome(), Toast.LENGTH_SHORT).show();
            PlayingNow = musica;
            listaPlayingNow = listaMusicas;
            stopUpdateThread();
            //TODO MUSIC CODE
            try {

                playerManager.setMusicPlay(PlayingNow, listaPlayingNow, 1);

                updateScreenComponents();
                startUpdateThread();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            WhereAreWe = musica.getArquivo();
            carregarMusicas(musica.getArquivo(), false);
            Toast.makeText(this, "indo para: " + musica.getNome(), Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "Tocando: " + musica.getNome(), Toast.LENGTH_SHORT).show();
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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TrafficStats.setThreadStatsTag((int) Thread.currentThread().getId());
                        try {

                            ccont.cadastrarMusica(musica);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();


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