package com.paam.songbook.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentSong == null) return

    val targetProgress = if (duration > 0) position / duration.toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = targetProgress, label = "miniPlayerProgress")

    val painter = rememberAsyncImagePainter(currentSong.albumArt)

    // --- SOLUTION ---
    // Change the Surface to a Box with a specific background color that matches your app's dark theme.
    // This makes the player look intentional and removes the "empty space" feeling.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1B2A).copy(alpha = 0.95f)) // A dark, slightly translucent color
            .clickable { onExpand() }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        currentSong.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        color = Color.White
                        // Explicitly set text color to white
                    )
                    Text(
                        currentSong.artist,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        color = Color.White.copy(alpha = 0.7f) // Lighter white for subtitle
                    )
                }

                // Set icon tints to white for visibility
                IconButton(onClick = onPlayPause) {
                    Crossfade(targetState = isPlaying, label = "playPauseAnim") { playing ->
                        Icon(
                            if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White
                        )
                    }
                }
                IconButton(onClick = onNext) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }

            if (duration > 0) {
                LinearProgressIndicator(
                    progress = { animatedProgress }, // Correct lambda syntax for newer versions
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}
