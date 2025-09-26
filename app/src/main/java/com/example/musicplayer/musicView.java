package com.example.musicplayer;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class musicView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_view);


        ImageView AlbumCoverPlaying2 = findViewById(R.id.AlbumCoverPlaying2);
        TextView MusicPlaying2 = findViewById(R.id.MusicPlaying2);
        TextView artistPlaying2 = findViewById(R.id.artistPlaying2);
        if (MainActivity.PlayingNow != null ){
            AlbumCoverPlaying2.setImageBitmap(MainActivity.PlayingNow.getCapaAlbum());
            MusicPlaying2.setText(MainActivity.PlayingNow.getNome());
            artistPlaying2.setText(MainActivity.PlayingNow.getArtista());
        }



    }
}