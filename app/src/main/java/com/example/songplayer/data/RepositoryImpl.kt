package com.example.songplayer.data

import android.app.Application
import com.example.songplayer.domain.Music
import com.example.songplayer.domain.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepositoryImpl(private val application: Application): Repository {


    private val musicDao = AppDatabase.getInstance(application).userDao()
    private val musicMapper = MusicMapper()

    override suspend fun addMusic(music: Music) {
        musicDao.addMusic(musicMapper.mapEntityToDbModel(music))
    }

    override suspend fun deleteMusic(id: Int) {
        musicDao.deleteMusic(id)
    }

    override suspend fun getMusic(id: Int): Music {
        return musicMapper.mapDbModelToEntity(musicDao.getMusic(id))
    }

    override suspend fun getMusicList(): Flow<List<Music>> {
        return musicDao.getMusicList().map {
            musicMapper.mapListDbModelToListEntity(it)
        }
    }

}