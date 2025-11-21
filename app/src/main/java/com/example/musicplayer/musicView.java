package com.example.musicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.musicplayer.Managers.playerManager;
import com.google.android.material.button.MaterialButton;

public class musicView extends AppCompatActivity {

    private MediaMetadataRetriever mp3Info = new MediaMetadataRetriever();
    private SeekBar seekBar;
    private ImageButton forwardButton;
    private ImageButton playButton;
    private ImageButton backButton;
    private Button favoriteButton;
    private Button playlistButton;
    private Button queueButton;
    private ImageView AlbumCoverPlaying2;
    private TextView MusicPlaying2;
    private TextView artistPlaying2;
    private MaterialButton orderButton;
    private Thread updateThread;
    public boolean isRunning = false;
    playerManager playerManager;
    private Handler handler = new Handler();
    private Runnable runnableCode;
    private static final int INTERVAL = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_view);

        seekBar = findViewById(R.id.seekBar2);
        AlbumCoverPlaying2 = findViewById(R.id.AlbumCoverPlaying2);
        MusicPlaying2 = findViewById(R.id.MusicPlaying2);
        artistPlaying2 = findViewById(R.id.artistPlaying2);
        playerManager = MainActivity.playerManager;
        setupSeekBar();


        forwardButton = findViewById(R.id.forwardButton2);
        playButton = findViewById(R.id.playButton2);
        backButton = findViewById(R.id.BackButton2);
        favoriteButton = findViewById(R.id.favoriteButton);
        playlistButton = findViewById(R.id.playstButton);
        queueButton = findViewById(R.id.playingNowButton);
        orderButton = findViewById(R.id.orderButton);

        UpdateUi();

        if (playerManager.isPlaying()) {
            startUpdateThread();
        }


        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerManager.setMode(playerManager.getMode() + 1);
                UpdateUi();
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopUpdateThread();
                playerManager.nextMediaPlayer();
                startUpdateThread();
                MainActivity.PlayingNow = playerManager.music;
                UpdateUi();
            }
        });

        playButton.setOnClickListener(v -> {
            // Play/Pause
            if (playerManager != null) {
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
            stopUpdateThread();
            playerManager.prevMediaPlayer();
            startUpdateThread();
            MainActivity.PlayingNow = playerManager.music;
            UpdateUi();
        });

        runnableCode = () -> {
            // Put your periodic command/task here
            Log.d("PeriodicTask", "Running");
            UpdateUi();
            // Reschedule the same runnable code block again
            handler.postDelayed(runnableCode, INTERVAL);
        };

        handler.post(runnableCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableCode);

        // **Crucial: Unregister/remove any custom listeners or clear resources here**
        // e.g., if you have a sensor listener, unregister it:
        // sensorManager.unregisterListener(this);
        // If you have a Firebase listener, remove it:
        // if (listenerRegistration != null) {
        //     listenerRegistration.remove();
        // }

        // This prevents memory leaks
    }

    private void UpdateUi() {

        if (MainActivity.PlayingNow != null) {
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

        if (playerManager.isPlaying()) {
            playButton.setImageResource(R.drawable.pause_circle);
        } else {
            playButton.setImageResource(R.drawable.play_circle);
        }

        // 1 = go
        // 2 = go repeating
        // 3 = shuffle
        // 4 = real shuffle
        // 5 = repeat one

        switch (playerManager.getMode()) {
            case 1:

                orderButton.setIconResource(R.drawable.no_repeat);
                backButton.setActivated(true);
                break;
            case 2:
                orderButton.setIconResource(R.drawable.repeat);
                backButton.setActivated(true);
                break;
            case 3:
                orderButton.setIconResource(R.drawable.shuffle);
                backButton.setActivated(true);
                break;
            case 4:
                orderButton.setIconResource(R.drawable.autoplay);
                backButton.setActivated(false);
                break;
            case 5:
                orderButton.setIconResource(R.drawable.repeat_one);
                backButton.setActivated(true);
                break;
            default:
                break;
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