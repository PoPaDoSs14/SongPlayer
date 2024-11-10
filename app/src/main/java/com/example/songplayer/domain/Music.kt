package com.example.songplayer.domain

import android.net.Uri

data class Music(
    val id: Int,
    val name: String,
    val musicLink: Uri
)