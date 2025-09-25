package com.example.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.database.AppDatabase;
import com.example.musicplayer.database.Usuario;
import com.example.yourapp.MP3FolderScanner;
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
    private NavigationView sideMenu;
    private RecyclerView recyclerView;
    private MediaMetadataRetriever mp3Info = new MediaMetadataRetriever();
    private MusicaAdapter musicaAdapter;
    private List<Musica> listaMusicas;
    private List<Musica> listaMusicasOnline;
    private List<String> listaPastasMusica;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        //Declarar botoes
        sideMenuButton = findViewById((R.id.sideMenuButton));
        sideMenu = findViewById(R.id.navBarLateral);

        permissionHelper = new StoragePermissionHelper(this);

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
                //Toast.makeText(MainActivity.this, "Permissão negada. Não é possível acessar arquivos.", Toast.LENGTH_LONG).show();
            }
        });

        //Banco de dados
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket cliente = new Socket("192.168.56.1", 12345);
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
            ccont.usuarioLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }


        //Musicas
        recyclerView = findViewById(R.id.recycler_view_musicas);
        listaMusicas = new ArrayList<>();
        listaPastasMusica = new ArrayList<>();
        setupRecyclerView();
        carregarMusicas(null);

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

    }

    //Recycler view stuff --->
    private void setupRecyclerView() {
        listaMusicas = new ArrayList<>();
        musicaAdapter = new MusicaAdapter(this, listaMusicas);
        musicaAdapter.setOnMusicaClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicaAdapter);

    }

    private void carregarMusicas(String folderToLoad) {
        // Dados de exemplo - substitua pela sua fonte de dados

        List<String> mp3Folders = new ArrayList<>();

        if (folderToLoad == null) {
            MP3FolderScanner scanner = new MP3FolderScanner(context);
            mp3Folders = scanner.scanForMP3Folders();

            // Os diretórios ficam disponíveis na lista

            for (String folder : mp3Folders) {
                Log.d("MP3Folders", "Pasta encontrada: " + folder);
                List<String> temp = MP3Scanner.scanMp3Files(folder, false);
                listaMusicas.add(new Musica(false, folder.substring(folder.lastIndexOf('/') + 1), folder, String.valueOf(temp.size())));
                listaPastasMusica.add(folder);
            }
        } else {

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
                    listaMusicas.add(new Musica(
                            title,
                            author,
                            duracao,
                            folder,
                            albumArt
                    ));

                } else {
                    //add song
                    listaMusicas.add(new Musica(
                            title,
                            author,
                            duracao,
                            folder
                    ));
                }


            }
        }


//        listaMusicas.add(new Musica("Bohemian Rhapsody", "Queen", "5:55",null,null));
//        listaMusicas.add(new Musica("Hotel California", "Eagles", "6:30",null,null  ));
//        listaMusicas.add(new Musica("Stairway to Heaven", "Led Zeppelin", "8:02",null,null));
//        listaMusicas.add(new Musica("Imagine", "John Lennon", "3:03",null,null));
//        listaMusicas.add(new Musica("Sweet Child O' Mine", "Guns N' Roses", "5:03",null,null));
//        listaMusicas.add(new Musica("Billie Jean", "Michael Jackson", "4:54",null,null));
//        listaMusicas.add(new Musica("Like a Rolling Stone", "Bob Dylan", "6:13",null,null));
//        listaMusicas.add(new Musica("Smells Like Teen Spirit", "Nirvana", "5:01",null,null));

        // Notificar o adapter sobre as mudanças
        //musicaAdapter.notifyDataSetChanged();

        musicaAdapter.updateList(listaMusicas);
    }


    public void onMusicaClick(Musica musica, int position) {
        // Ação quando o card da música é clicado


        if (musica.isMusic()) {
            Toast.makeText(this, "Tocando: " + musica.getNome(), Toast.LENGTH_SHORT).show();
        } else {
            carregarMusicas(musica.getArquivo());
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