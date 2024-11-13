package com.example.songplayer.presentation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SongApp(navController: NavHostController, playlistViewModel: PlaylistViewModel, context: Context) {


    NavHost(
        navController = navController,
        startDestination = "PlaylistScreen")
    {
        composable("PlaylistScreen") { PlaylistScreen(playlistViewModel, navController) }
        composable("MusicPlayerScreen/{musicId}") { backStackEntry ->
            val musicId = backStackEntry.arguments?.getString("musicId")
            val music = playlistViewModel.getMusicById(musicId)
            MusicPlayerScreen(music)
        }
    }
}