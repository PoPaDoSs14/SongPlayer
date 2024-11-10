package com.example.songplayer.domain

import kotlinx.coroutines.flow.Flow

interface Repository {


    suspend fun addMusic(music: Music)

    suspend fun deleteMusic(id: Int)

    suspend fun getMusic(id: Int): Music

    suspend fun getMusicList(): Flow<List<Music>>
}