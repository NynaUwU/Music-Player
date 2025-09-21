package com.example.musicplayer;

import androidx.room.*;


import java.util.List;

public interface MusicDao {

    @Insert
    void inserir(Musica music);

    @Query("") List<Musica> listarTodos();
}
