package com.paam.songbook.ui.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun PlaylistScreen(
    playlists: List<String>,
    onPlaylistSelected: (String) -> Unit
) {
    Column {
        playlists.forEach { playlist ->
            Text(
                text = playlist,
                modifier = androidx.compose.ui.Modifier
                    .clickable { onPlaylistSelected(playlist) }
                    .padding(16.dp)
            )
        }
    }
}
