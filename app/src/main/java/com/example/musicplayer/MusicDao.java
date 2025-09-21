package com.example.musicplayer;


import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface MusicDao {

    @Insert
    void inserir(Musica music);

    //@Query("") List<Musica> listarTodos();
}
