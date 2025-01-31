package com.example.songplayer.data

import androidx.core.net.toUri
import com.example.songplayer.domain.Music

class MusicMapper {

    fun mapEntityToDbModel(music: Music): MusicDbModel{
        return MusicDbModel(
            id = music.id,
            name = music.name,
            artist = music.artist,
            musicLink = music.musicLink.toString()
        )
    }

    fun mapDbModelToEntity(musicDbModel: MusicDbModel): Music {
        return Music(
            id = musicDbModel.id,
            name = musicDbModel.name,
            artist = musicDbModel.artist,
            musicLink = musicDbModel.musicLink.toUri()
        )
    }


    fun mapListDbModelToListEntity(list: List<MusicDbModel>): List<Music> = list.map {
        mapDbModelToEntity(it)
    }

}