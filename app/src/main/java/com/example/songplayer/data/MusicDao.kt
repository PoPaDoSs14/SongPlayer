package com.example.songplayer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Insert
    fun addMusic(musicDbModel: MusicDbModel)

    @Query("DELETE FROM music WHERE id=:id")
    fun deleteMusic(id: Int)

    @Query("SELECT * FROM music WHERE id=:id LiMIT 1")
    fun getMusic(id: Int): MusicDbModel

    @Query("SELECT * FROM music")
    fun getMusicList(): Flow<List<MusicDbModel>>
}