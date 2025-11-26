package com.paam.songbook.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.paam.songbook.Model.Song
import com.paam.songbook.media.toMediaItem

@Composable
fun SongTile(
    song: Song,
    controller: MediaController,   // ✅ Correct type
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            .border(1.dp, Color.White, RoundedCornerShape(12.dp))
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clickable {
                val mediaItems = listOf(song.toMediaItem())
                controller.setMediaItems(mediaItems, 0, controller.currentPosition)
                controller.prepare()
                controller.play()
            }
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                maxLines = 1
            )
        }

        Crossfade(
            targetState = controller.isPlaying && controller.currentMediaItem?.mediaId == song.songID.toString(),
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(8.dp)
        ) { isPlaying ->
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White
            )
        }
    }
}
