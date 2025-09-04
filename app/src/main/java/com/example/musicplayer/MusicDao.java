package com.example.musicplayer;

import androidx.room.Dao;
import androidx.room.Import;
import androidx.room.Query;

import java.util.List;

public interface MusicDao {

    @Insert
    void inserir(Musica music);

    @Query("") List<Musica> listarTodos();
}
