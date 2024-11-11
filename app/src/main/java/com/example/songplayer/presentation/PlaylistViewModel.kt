package com.example.songplayer.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.songplayer.data.RepositoryImpl
import com.example.songplayer.domain.Music

class PlaylistViewModel(application: Application): AndroidViewModel(application) {


    private val repo = RepositoryImpl(application)

    private val _musicList = MutableLiveData<List<Music>>(emptyList())
    val musicList: LiveData<List<Music>> get() = _musicList

}