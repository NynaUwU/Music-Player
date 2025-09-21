package com.example.musicplayer;

import androidx.room.*;

@database (entities = {Musica.class}, version=1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MusicDao musicDao();

}
