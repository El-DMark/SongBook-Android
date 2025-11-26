package com.paam.songbook.ui.songs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paam.songbook.Model.Song
import com.paam.songbook.ui.home.SongTile

@Composable
fun SongCarousel(
    songs: List<Song>,
    controller: androidx.media3.session.MediaController
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        songs.forEach { song ->
            SongTile(
                song = song,
                controller = controller,
                modifier = Modifier
                    .width(140.dp)
                    .height(140.dp)
            )
        }
    }
}
