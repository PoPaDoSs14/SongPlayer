package com.example.songplayer.presentation

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SongApp(navController: NavHostController, getContent: ActivityResultLauncher<Intent>,
            playlistViewModel: PlaylistViewModel, context: Context) {


    NavHost(
        navController = navController,
        startDestination = "PlaylistScreen")
    {
        composable("PlaylistScreen") { PlaylistScreen(playlistViewModel, navController, getContent) }
        composable("MusicPlayerScreen/{musicId}") { backStackEntry ->
            val musicId = backStackEntry.arguments?.getString("musicId")
            val music = playlistViewModel.getMusicById(musicId)

            MusicPlayerScreen(
                initialMusic = music,
                onNext = {
                    val nextMusic = playlistViewModel.getNextMusic(music)
                    if (nextMusic != null) {
                        navController.navigate("MusicPlayerScreen/${nextMusic.id}")
                    }
                },
                onPrevious = {
                    // Реализуйте логику для нажатия на предыдущий трек здесь
                }
            )
        }
    }
}