package com.example.songplayer.presentation

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.songplayer.domain.Music
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(music: Music, onPrevious: () -> Unit, onNext: () -> Unit) {
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }

    LaunchedEffect(music) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(music.musicLink.toString()) // тут может быть ошибка
        mediaPlayer.prepare()
        duration = mediaPlayer.duration

        while (isPlaying) {
            currentPosition = mediaPlayer.currentPosition
            delay(1000)
        }

        mediaPlayer.setOnCompletionListener {
            isPlaying = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = music.name,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = music.artist,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LinearProgressIndicator(
            progress = currentPosition.toFloat() / duration,
            modifier = Modifier.fillMaxWidth().height(4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                onPrevious()
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Предыдущий трек")
            }

            IconButton(onClick = {
                isPlaying = !isPlaying
                if (isPlaying) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.pause()
                }
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Пауза" else "Воспроизведение"
                )
            }

            IconButton(onClick = {
                onNext()
            }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Следующий трек")
            }
        }
    }
}