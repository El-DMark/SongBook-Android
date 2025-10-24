package com.paam.songbook.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.ui.home.HomeScreen

@Composable
fun HomeHost(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    navController: NavController,
    controller: MediaController,
    query: String, // 🔹 add this
    modifier: Modifier = Modifier
) {
    HomeScreen(
        songs = songs,
        onSongSelected = onSongSelected,
        navController = navController,
        controller = controller,
        query = query,   // 🔹 pass it down
        modifier = modifier
    )
}


fun extractFolder(path: String): String {
    val cleaned = path.replace("file://", "")
    val segments = cleaned.split('/', '\\').filter { it.isNotBlank() }
    return if (segments.size > 1) segments[segments.size - 2] else "Unknown"
}