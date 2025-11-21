package com.example.musicplayer.Managers;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.Musica;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class playerManager implements Runnable {
    private final MainActivity mainActivity;
    private Context context;
    private boolean playing = false;
    private Random random = new Random();
    public MediaPlayer mediaPlayer;
    private List<Musica> oldListaPlayingNow;
    private List<Musica> listaPlayingNow;
    private int mode = 1;
    // 1 = go
    // 2 = go repeating
    // 3 = shuffle
    // 4 = real shuffle
    // 5 = repeat one
    public Musica music;

    public playerManager(Context context, MainActivity activity) {
        this.context = context;
        this.mainActivity = activity;
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void run() {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer = mp;
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
            playing = true;
            return true;
        } else {
            playing = false;
            return false;
        }

    }

    public void playPausePlayback() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mainActivity.stopUpdateThread();
            mediaPlayer.pause();
            playing = false;
        } else if (mediaPlayer != null) {
            mediaPlayer.start();
            mainActivity.startUpdateThread();
            playing = true;
        }
    }

    public void prevMediaPlayer() {
        if (listaPlayingNow != null) {
            int here = listaPlayingNow.indexOf(music);

            mainActivity.stopUpdateThread();

            switch (mode) {
                case 1:
                    if (here == 0) {
                        StopPlayer();
                    } else {
                        here--;
                        try {
                            File file = new File(listaPlayingNow.get(here).getArquivo());
                            Uri uri = Uri.fromFile(file);
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(this.context, uri);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            playing = true;

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            music = listaPlayingNow.get(here);
                            mainActivity.startUpdateThread();
                        }
                    }
                    break;
                case 2:
                case 3:
                    if (here == 0) {
                        here = listaPlayingNow.size();
                    } else {
                        here--;
                        try {
                            File file = new File(listaPlayingNow.get(here).getArquivo());
                            Uri uri = Uri.fromFile(file);
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(this.context, uri);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            playing = true;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            music = listaPlayingNow.get(here);
                        }
                    }
                    mainActivity.startUpdateThread();
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
                        playing = true;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        music = listaPlayingNow.get(here);
                    }
                    mainActivity.startUpdateThread();
                    break;
                case 5:
                    try {
                        File file = new File(listaPlayingNow.get(here).getArquivo());
                        Uri uri = Uri.fromFile(file);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(this.context, uri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        playing = true;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    break;
                default:

                    break;
            }
            mainActivity.PlayingNow=listaPlayingNow.get(here);
            mainActivity.updateScreenComponents();
        }
    }

    public void nextMediaPlayer() {
        if (listaPlayingNow != null) {
            int here = listaPlayingNow.indexOf(music);

            mainActivity.stopUpdateThread();

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
                            playing = true;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            music = listaPlayingNow.get(here);
                            mainActivity.startUpdateThread();
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
                            playing = true;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            music = listaPlayingNow.get(here);
                        }
                    }
                    mainActivity.startUpdateThread();
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
                        playing = true;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        music = listaPlayingNow.get(here);
                    }
                    mainActivity.startUpdateThread();
                    break;
                case 5:
                    try {
                        File file = new File(listaPlayingNow.get(here).getArquivo());
                        Uri uri = Uri.fromFile(file);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(this.context, uri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        playing = true;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    break;
                default:

                    break;
            }
            mainActivity.PlayingNow=listaPlayingNow.get(here);
            mainActivity.updateScreenComponents();
        }
    }


    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        if (mode >= 6) {
            mode = 1;
        }

        this.mode = mode;
        if (mode == 3) {
            Collections.shuffle(listaPlayingNow);
        } else {
            listaPlayingNow = oldListaPlayingNow;
        }
        Log.d("MP3", "mode: " + this.mode);
    }

    public List<Musica> getListaPlayingNow() {
        return listaPlayingNow;
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
        playing = true;
    }

    public void setProgress(int percent) {
        mediaPlayer.seekTo(percent);
    }

    public int getProgress() {
        if (music == null) {
            return 100;
        } else {
            return mediaPlayer.getCurrentPosition();
        }
    }

    public int getTotalTime() {
        if (music == null) {
            return 100;
        } else {
            try {

                return mediaPlayer.getDuration();
            } catch (Exception ignored) {
                return 100;
            }
        }
    }

    private void StopPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        playing = false;
        //mediaPlayer.release();
    }

    public boolean isPlaying() {
        return playing;
    }
}
