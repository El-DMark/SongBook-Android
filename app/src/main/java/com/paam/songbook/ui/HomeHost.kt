package com.paam.songbook.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.ui.home.HomeScreen

@Composable
fun HomeHost(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    HomeScreen(
        songs = songs,
        onSongSelected = onSongSelected,
        navController = navController,
        modifier = modifier
    )
}


fun extractFolder(path: String): String {
    val cleaned = path.replace("file://", "")
    val segments = cleaned.split('/', '\\').filter { it.isNotBlank() }
    return if (segments.size > 1) segments[segments.size - 2] else "Unknown"
}