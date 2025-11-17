package com.example.musicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.musicplayer.Managers.playerManager;

public class musicView extends AppCompatActivity {

    private MediaMetadataRetriever mp3Info = new MediaMetadataRetriever();
    private SeekBar seekBar;
    private Thread updateThread;
    public boolean isRunning = false;
    playerManager playerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_view);

        seekBar = findViewById(R.id.seekBar2);
        playerManager = MainActivity.playerManager;
        setupSeekBar();

        ImageButton forwardButton = findViewById(R.id.forwardButton2);
        ImageButton playButton = findViewById(R.id.playButton2);
        ImageButton backButton = findViewById(R.id.BackButton2);



        ImageView AlbumCoverPlaying2 = findViewById(R.id.AlbumCoverPlaying2);
        TextView MusicPlaying2 = findViewById(R.id.MusicPlaying2);
        TextView artistPlaying2 = findViewById(R.id.artistPlaying2);
        if (MainActivity.PlayingNow != null ){
            if (MainActivity.PlayingNow.getCapaAlbum()) {
                mp3Info.setDataSource(MainActivity.PlayingNow.getArquivo());

                byte[] albumArtBytes = mp3Info.getEmbeddedPicture();
                Bitmap albumArt = null;
                if (albumArtBytes != null) {
                    albumArt = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
                    AlbumCoverPlaying2.setImageBitmap(albumArt);
                }
            }

            //AlbumCoverPlaying2.setImageBitmap(MainActivity.PlayingNow.getCapaAlbum());
            MusicPlaying2.setText(MainActivity.PlayingNow.getNome());
            artistPlaying2.setText(MainActivity.PlayingNow.getArtista());
        }


        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopUpdateThread();
                playerManager.nextMediaPlayer();
                startUpdateThread();
                MainActivity.PlayingNow = playerManager.music;

                ImageView AlbumCoverPlaying2 = findViewById(R.id.AlbumCoverPlaying2);
                TextView MusicPlaying2 = findViewById(R.id.MusicPlaying2);
                TextView artistPlaying2 = findViewById(R.id.artistPlaying2);
                if (MainActivity.PlayingNow != null ){
                    if (MainActivity.PlayingNow.getCapaAlbum()) {
                        mp3Info.setDataSource(MainActivity.PlayingNow.getArquivo());

                        byte[] albumArtBytes = mp3Info.getEmbeddedPicture();
                        Bitmap albumArt = null;
                        if (albumArtBytes != null) {
                            albumArt = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
                            AlbumCoverPlaying2.setImageBitmap(albumArt);
                        }
                    }

                    //AlbumCoverPlaying2.setImageBitmap(MainActivity.PlayingNow.getCapaAlbum());
                    MusicPlaying2.setText(MainActivity.PlayingNow.getNome());
                    artistPlaying2.setText(MainActivity.PlayingNow.getArtista());
                }

                if(playerManager.isPlaying()){
                    playButton.setImageResource(R.drawable.pause_circle);
                }else {
                    playButton.setImageResource(R.drawable.play_circle);
                }
            }
        });

        playButton.setOnClickListener(v -> {
            // Play/Pause
            if(playerManager !=null) {
                playerManager.playPausePlayback();
                if (playerManager.isPlaying()) {
                    playButton.setImageResource(R.drawable.pause_circle);
                } else {
                    playButton.setImageResource(R.drawable.pause_circle);
                }
            }
        });

        backButton.setOnClickListener(v -> {
            // Faixa anterior
            // TODO rewind button
        });

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
}