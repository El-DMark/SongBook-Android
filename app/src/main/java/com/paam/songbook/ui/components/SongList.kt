package com.paam.songbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paam.songbook.model.Song

@Composable
fun SongList(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    useGrid: Boolean = true
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        if (isLoading) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(6) {
                    ShimmerSongCard()
                }
            }
        } else if (songs.isEmpty()) {
            Text(
                text = "No songs found.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = if (useGrid) GridCells.Fixed(2) else GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(songs) { song ->
                    SongCard(
                        song = song,
                        onClick = { onSongSelected(song) }
                    )
                }
            }
        }
    }
}
