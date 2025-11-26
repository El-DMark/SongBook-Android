package com.paam.songbook.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paam.songbook.Model.Song

@Composable
fun GroupedList(
    grouped: Map<String, List<Song>>,
    onSongSelected: (Song) -> Unit
) {
    LazyColumn {
        grouped.forEach { (group, groupSongs) ->
            item {
                Text(
                    text = group,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
            items(groupSongs) { song ->
                SongRow(song, onClick = { onSongSelected(song) })
            }
        }
    }
}
