package com.paam.songbook.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paam.songbook.model.Song

@Composable
fun FeaturedRow(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit
) {
    Text(
        "Featured",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        if (songs.isEmpty()) {
            items(5) { AlbumCardSkeleton() }
        } else {
            items(songs.take(10)) { song ->
                AlbumCard(song = song, onClick = { onSongSelected(song) })
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}
