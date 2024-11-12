package com.example.songplayer.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.songplayer.domain.Music

@Composable
fun PlaylistScreen(viewModel: PlaylistViewModel) {
    val context = LocalContext.current
    val musicList by viewModel.musicList.observeAsState(emptyList())

    val pickAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                viewModel.addMusic(it)
            }
        }
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                pickAudioLauncher.launch("audio/*")
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить музыку")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(innerPadding)
        ) {
            items(musicList) { music ->
                MusicItem(music)
            }
        }
    }
}

@Composable
fun MusicItem(music: Music) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { },
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