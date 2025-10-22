    package com.paam.songbook.ui.components

    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyRow
    import androidx.compose.foundation.lazy.items
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import com.paam.songbook.model.Song

    @Composable
    fun NewlyAddedCarousel(
        songs: List<Song>,
        onSongSelected: (Song) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val sortedSongs = songs.sortedByDescending { it.timestamp }

        Column(modifier = modifier.padding(vertical = 12.dp)) {
            Text(
                text = "Newly Added",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                            onClick = { onSongSelected(song) }
                        )
                    }
                }
            }
        }
    }

