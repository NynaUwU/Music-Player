package com.example.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class musicView extends AppCompatActivity {

    private MediaMetadataRetriever mp3Info = new MediaMetadataRetriever();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_view);


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



    }
}