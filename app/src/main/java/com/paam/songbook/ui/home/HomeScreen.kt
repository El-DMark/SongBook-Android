package com.paam.songbook.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.ui.songs.FeaturedSongCard
import com.paam.songbook.ui.songs.SongCarousel

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
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Featured Song
        if (featuredSong != null) {
            FeaturedSongCard(song = featuredSong)
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 🔹 Recently Added
        Text("Recently Added", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        SongCarousel(
            songs = songs.take(6),
            controller = controller
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Popular Hymns
        Text("Most Listened", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        SongCarousel(
            songs = songs.reversed().take(6),
            controller = controller
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
