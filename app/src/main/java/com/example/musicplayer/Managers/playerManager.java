package com.example.musicplayer.Managers;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

public class playerManager implements Runnable {
    private Context context;
    private MediaPlayer mediaPlayer;

    public playerManager(Context context) {
        this.context = context;

    }

    @Override
    public void run() {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    releaseMediaPlayer();
                }
            });
        }
    }

    public boolean setMusicPlay(String arquive) throws IOException {
        if (mediaPlayer != null) {
            File file = new File(arquive);
            Uri uri = Uri.fromFile(file);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this.context, uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            return true;
        } else {
            return false;
        }
    }

    public void playPausePlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }


    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
