package com.paam.songbook.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import coil.compose.rememberAsyncImagePainter

@Composable
fun MusicPlayerScreen(
    controller: MediaController?,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongSelected: (Song) -> Unit,
    onMiniPlayerClick: () -> Unit,
    modifier: Modifier = Modifier   // 👈 added modifier
) {
    val songs = sampleSongs()

    LazyColumn(
        modifier = modifier.fillMaxSize()   // 👈 apply modifier here
    ) {
        items(songs) { song ->
            SongRow(
                song = song,
                onClick = { onSongSelected(song) }
            )
        }
    }
}

@Composable
fun SongRow(
    song: Song,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(song.albumArt),
            contentDescription = null,
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


fun sampleSongs(): List<Song> = listOf(
    Song(
        title = "SoundHelix Song 1",
        artist = "Artist A",
        url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        albumArt = "https://picsum.photos/200/200?1"
    ),
    Song(
        title = "SoundHelix Song 2",
        artist = "Artist B",
        url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        albumArt = "https://picsum.photos/200/200?2"
    ),
    Song(
        title = "SoundHelix Song 3",
        artist = "Artist C",
        url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        albumArt = "https://picsum.photos/200/200?3"
    )
)
