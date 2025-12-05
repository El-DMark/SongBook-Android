package com.paam.songbook.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.ui.songs.FeaturedSongCard
import com.paam.songbook.ui.songs.MostListenedSection // 👈 Import new composable
import com.paam.songbook.ui.songs.RecentlyAddedSection // 👈 Import new composable

@Composable
fun HomeScreen(
    songs: List<Song>,
    navController: NavController,
    controller: MediaController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val featuredSong = songs.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF2C5364))
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp) // Use Arrangement for consistent spacing
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Featured Song
        if (featuredSong != null) {
            FeaturedSongCard(song = featuredSong)
        }

        // 🔹 Recently Added (Using the new composable)
        RecentlyAddedSection(
            songs = songs,
            controller = controller
        )

        // 🔹 Most Listened (Using the new composable)
        MostListenedSection(
            songs = songs,
            controller = controller
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
