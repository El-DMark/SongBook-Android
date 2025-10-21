package com.paam.songbook.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.paam.songbook.model.Song

@Composable
fun MiniPlayer(
    currentSong: Song?,
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    onExpand: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit
) {
    if (currentSong == null) return

    // ✅ Animate progress smoothly
    val targetProgress = if (duration > 0) position / duration.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        label = "miniPlayerProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpand() }
    ) {
        // Top row: album art + info + controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art thumbnail
            Image(
                painter = rememberAsyncImagePainter(currentSong.albumArt),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            // Song info
            Column(Modifier.weight(1f)) {
                Text(
                    currentSong.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    currentSong.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Controls
            IconButton(onClick = onPlayPause) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause"
                )
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next")
            }
        }

        // Slim animated progress bar
        if (duration > 0) {
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
