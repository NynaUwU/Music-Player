package com.example.musicplayer.Managers;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.musicplayer.Musica;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class playerManager implements Runnable {
    private Context context;
    private Random random = new Random();
    private MediaPlayer mediaPlayer;
    private List<Musica> oldListaPlayingNow;
    private List<Musica> listaPlayingNow;
    private int mode = 1;
    // 1 = go
    // 2 = go repeating
    // 3 = shuffle
    // 4 = real shuffle
    // 5 = repeat one
    public Musica music;

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
                    nextMediaPlayer();
                }
            });
        }
    }

    public boolean setMusicPlay(Musica music, List<Musica> listaPlayingNow, int mode) throws IOException {
        this.listaPlayingNow = listaPlayingNow;
        this.oldListaPlayingNow = listaPlayingNow;
        this.music = music;
        this.mode = mode;
        if (mediaPlayer != null) {
            File file = new File(music.getArquivo());
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


    public void nextMediaPlayer() {
        int here = listaPlayingNow.indexOf(music);

        switch (mode) {
            case 1:
                if (here + 1 == listaPlayingNow.size()) {
                    StopPlayer();
                } else {
                    here++;
                    try {
                        File file = new File(listaPlayingNow.get(here).getArquivo());
                        Uri uri = Uri.fromFile(file);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(this.context, uri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        music = listaPlayingNow.get(here);
                    }
                }
                break;
            case 2:
            case 3:
                if (here + 1 == listaPlayingNow.size()) {
                    here = 0;
                } else {
                    here++;
                    try {
                        File file = new File(listaPlayingNow.get(here).getArquivo());
                        Uri uri = Uri.fromFile(file);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(this.context, uri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        music = listaPlayingNow.get(here);
                    }
                }
                break;
            case 4:
                here = random.nextInt(listaPlayingNow.size());
                try {
                    File file = new File(listaPlayingNow.get(here).getArquivo());
                    Uri uri = Uri.fromFile(file);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(this.context, uri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    music = listaPlayingNow.get(here);
                }
                break;
            case 5:
                mediaPlayer.stop();
                mediaPlayer.start();
                break;
            default:

                break;
        }

    }



    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        if (mode == 3) {
            Collections.shuffle(listaPlayingNow);
        } else {
            listaPlayingNow = oldListaPlayingNow;
        }
    }

    public List<Musica> getListaPlayingNow() {
        return listaPlayingNow;
    }

    public void setListaPlayingNow(List<Musica> listaPlayingNow) {
        this.listaPlayingNow = listaPlayingNow;
    }

    public void setMusic(Musica music) throws IOException {
        this.music = music;
        File file = new File(music.getArquivo());
        Uri uri = Uri.fromFile(file);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(this.context, uri);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    public void setProgress(int percent){
        mediaPlayer.seekTo(percent);
    }

    public int getProgress(){
        if (music==null){
            return 100;
        }else{
            return mediaPlayer.getCurrentPosition();
        }
    }

    public int getTotalTime(){
        if (music==null){
            return 100;
        }else{
            return mediaPlayer.getDuration();
        }
    }

    private void StopPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        //mediaPlayer.release();
    }
}
