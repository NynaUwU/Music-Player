package com.example.musicplayer;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@database(entities = {Musica.class}, version=1)
public abstract class AppDatabase extends RoomDataBase {

    public abstract MusicDao musicDao();

}
