package com.paam.songbook.ui.songs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.basicMarquee
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.paam.songbook.model.Song
import com.paam.songbook.media.toMediaItem
import androidx.media3.session.MediaController

@Composable
fun SongListScreen(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    controller: MediaController
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF2C5364))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = bottomInset + 72.dp // Padding for player controls
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(songs) { song ->
                // --- 2. CALL THE NEW COMPOSABLE HERE ---
                SongListItem(
                    song = song,
                    isPlaying = controller.isPlaying && controller.currentMediaItem?.mediaId == song.songID.toString(),
                    onPlayPauseClicked = {
                        if (controller.isPlaying && controller.currentMediaItem?.mediaId == song.songID.toString()) {
                            controller.pause()
                        } else {
                            // This logic assumes clicking play on a song plays only that song.
                            // To play from a list, this logic needs to be passed from the screen level.
                            onSongSelected(song)
                        }
                    },
                    onMoreClicked = { /* TODO: show menu */ },
                    onItemSelected = { onSongSelected(song) }
                )
            }
        }
    }
}

// --- 1. EXTRACTED THE ROW LOGIC INTO ITS OWN COMPOSABLE ---
@Composable
fun SongListItem(
    song: Song,
    isPlaying: Boolean,
    onPlayPauseClicked: () -> Unit,
    onMoreClicked: () -> Unit,
    onItemSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemSelected() } // Use the passed lambda
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔹 Album art
        if (!song.albumArt.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(song.albumArt),
                contentDescription = song.title,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 🔹 Song info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                maxLines = 1
            )
        }

        // 🔹 Actions
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPlayPauseClicked) { // Use the passed lambda
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White
                )
            }
            IconButton(onClick = onMoreClicked) { // Use the passed lambda
                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
            }
        }
    }
}
