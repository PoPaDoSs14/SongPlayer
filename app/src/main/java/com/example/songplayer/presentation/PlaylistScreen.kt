package com.example.songplayer.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.songplayer.domain.Music

@Composable
fun PlaylistScreen(musicList: List<Music>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(musicList) { music ->
            MusicItem(music)
        }
    }
}

@Composable
fun MusicItem(music: Music) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { /* Handle music item click, e.g. play music */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = music.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = music.artist,
            style = MaterialTheme.typography.bodySmall
        )
    }
}