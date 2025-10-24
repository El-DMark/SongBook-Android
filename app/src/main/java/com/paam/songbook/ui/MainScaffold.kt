package com.paam.songbook.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.model.SongRepository
import com.paam.songbook.model.toMediaItem
import com.paam.songbook.ui.main.MainScreen

@Composable
fun MainScaffold(
    controller: MediaController,
    navController: NavController,
    songs: List<Song>
) {
    PlayerHost(
        controller = controller,
        songs = songs
    ) {
        MainScreen(
            songs = songs,
            navController = navController,
            controller = controller
        )
    }
}

