package com.example.songplayer.presentation

import android.app.Application
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.songplayer.data.RepositoryImpl
import com.example.songplayer.domain.Music
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class PlaylistViewModel(application: Application): AndroidViewModel(application) {


    private val repo = RepositoryImpl(application)

    private val _musicList = MutableLiveData<List<Music>>(emptyList())
    val musicList: LiveData<List<Music>> get() = _musicList

    init {
        viewModelScope.launch {
            repo.getMusicList().collect { musicList ->
                _musicList.value = musicList
            }
        }
    }

    fun getNextMusic(currentMusic: Music?): Music? {
        val currentIndex = musicList.value!!.indexOf(currentMusic)
        return if (currentIndex >= 0 && currentIndex < musicList.value!!.size - 1) {
            musicList.value!![currentIndex + 1]
        } else {
            null
        }
    }

    fun getMusicById(id: String?): Music? {
        return _musicList.value?.find { it.id.toString() == id }
    }

    fun addMusic(uri: Uri) {
        viewModelScope.launch {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(getApplication(), uri)

            val name = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown"
            val artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown"

            val newMusic = Music(id = 0, name = name, artist = artist, musicLink = uri)

            repo.addMusic(newMusic)

            repo.getMusicList().collect { musicList ->
                _musicList.value = musicList
            }
        }
    }
}