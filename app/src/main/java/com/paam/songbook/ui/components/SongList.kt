package com.paam.songbook.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paam.songbook.model.Song

@Composable
fun SongList(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    isLoading: Boolean = false
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        if (isLoading) {
            items(6) { SongItemSkeleton() }
        } else {
            items(songs) { song ->
                SongRow(song = song, onClick = { onSongSelected(song) })
            }
        }
    }
}

@Composable
private fun SongItemSkeleton() {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(56.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
        androidx.compose.foundation.layout.Spacer(Modifier.width(12.dp))
        androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .height(18.dp)
                    .fillMaxWidth(0.6f)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.4f)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}
