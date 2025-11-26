package com.paam.songbook.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paam.songbook.Model.Song
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewlyAddedCarousel(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Helper to safely parse a date string
    fun parseDate(dateString: String): LocalDate =
        runCatching { LocalDate.parse(dateString, formatter) }
            .getOrDefault(LocalDate.MIN)

    // Sort songs by parsed date
    val sortedSongs = songs.sortedByDescending { parseDate(it.timestamp) }

    Column(modifier = modifier.padding(vertical = 0.dp)) {
        Text(
            text = "Newly Added",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(0.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (songs.isEmpty()) {
                // Show shimmer placeholders while loading
                items(10) {
                    ShimmerSongCard()
                }
            } else {
                // Show real song cards
                items(sortedSongs.take(10)) { song ->
                    SongCard(
                        song = song,
                        onClick = { onSongSelected(song) },
                         modifier = Modifier.width(80.dp)
                    )
                }
            }
        }
    }
}
