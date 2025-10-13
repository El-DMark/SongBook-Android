package com.paam.songbook.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.model.SongRepository
import com.paam.songbook.model.toMediaItem

@Composable
fun MainScaffold(controller: MediaController, navController: NavController) {
    val context = LocalContext.current
    var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var isExpanded by remember { mutableStateOf(false) }

    // Load songs once
    LaunchedEffect(Unit) {
        val jsonUrl = "https://drive.google.com/uc?export=download&id=1X6vU7zurfsh7im0jZ6r2Hd9x-ewZNn3h"
        songs = SongRepository.loadSongs(context, jsonUrl)
    }

    PlayerHost(
        controller = controller,
        songs = songs,
        isExpanded = isExpanded,
        onCollapse = { isExpanded = false }
    ) { modifier ->
        HomeHost(
            songs = songs,
            navController = navController,
            onSongSelected = { song ->
                val mediaItems = songs.map { it.toMediaItem() }
                val startIndex = songs.indexOf(song).coerceAtLeast(0)
                controller.setMediaItems(mediaItems, startIndex, 0L)
                controller.prepare()
                controller.play()
                isExpanded = true
            },
            modifier = modifier
        )
    }
}
