package com.example.songplayer.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("Music")
data class MusicDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val musicLink: String
)