package com.paam.songbook.ui.songs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.paam.songbook.model.Song

/**
 * A composable section that displays a title and a carousel of recently added songs.
 */
@Composable
fun RecentlyAddedSection(
    songs: List<Song>,
    controller: MediaController,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Recently Added", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        SongCarousel(
            // Business logic: "Recently Added" are the first 6 songs
            songs = songs.take(6),
            controller = controller
        )
    }
}

/**
 * A composable section that displays a title and a carousel of most listened-to songs.
 */
@Composable
fun MostListenedSection(
    songs: List<Song>,
    controller: MediaController,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Most Listened", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        SongCarousel(
            // Business logic: "Most Listened" are the last 6 songs, reversed
            songs = songs.reversed().take(6),
            controller = controller
        )
    }
}
