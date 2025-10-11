package com.paam.songbook.ui
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.QueueMusic

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import com.paam.songbook.model.Song
import com.paam.songbook.model.toMediaItem

@Composable
fun UnifiedPlayer(
    controller: MediaController,
    isExpanded: Boolean,
    songs: List<Song>
) {
    var isPlaying by remember { mutableStateOf(controller.isPlaying) }
    var metadata by remember { mutableStateOf(controller.mediaMetadata ?: MediaMetadata.EMPTY) }
    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                metadata = mediaMetadata
                duration = controller.duration.takeIf { it > 0 } ?: 0L
                position = controller.currentPosition.takeIf { it >= 0 } ?: 0L
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                duration = controller.duration.takeIf { it > 0 } ?: 0L
            }
        }
        controller.addListener(listener)
        onDispose { controller.removeListener(listener) }
    }

    LaunchedEffect(isPlaying, controller) {
        while (isActive) {
            if (isPlaying) {
                position = controller.currentPosition.takeIf { it >= 0 } ?: 0L
                duration = controller.duration.takeIf { it > 0 } ?: duration
            }
            delay(500L)
        }
    }

    val title = metadata.title?.toString().takeUnless { it.isNullOrBlank() } ?: "Unknown"
    val artist = metadata.artist?.toString().takeUnless { it.isNullOrBlank() } ?: "Unknown Artist"
    val artworkUri = metadata.artworkUri

    if (!isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(artworkUri),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = {
                if (controller.isPlaying) controller.pause() else controller.play()
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(16.dp))

            Image(
                painter = rememberAsyncImagePainter(artworkUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Column {
                val safeDuration = duration.coerceAtLeast(1L)
                val safePosition = position.coerceIn(0L, safeDuration)

                Slider(
                    value = safePosition.toFloat(),
                    onValueChange = { newValue -> position = newValue.toLong() },
                    onValueChangeFinished = { controller.seekTo(position) },
                    valueRange = 0f..safeDuration.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(safePosition), style = MaterialTheme.typography.bodySmall)
                    Text(formatTime(safeDuration), style = MaterialTheme.typography.bodySmall)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            // Transport controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { controller.seekToPreviousMediaItem() }) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(36.dp))
                }
                IconButton(onClick = {
                    if (controller.isPlaying) controller.pause() else controller.play()
                }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { controller.seekToNextMediaItem() }) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next", modifier = Modifier.size(36.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Extra controls: Play All, Shuffle, Repeat
            // Extra controls: Play All, Shuffle, Repeat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play All
                IconButton(onClick = {
                    val mediaItems = songs.map { it.toMediaItem() }
                    controller.setMediaItems(mediaItems)
                    controller.prepare()
                    controller.play()
                }) {
                    Icon(Icons.Filled.QueueMusic, contentDescription = "Play All")
                }

                // Shuffle
                IconButton(onClick = {
                    controller.shuffleModeEnabled = !controller.shuffleModeEnabled
                }) {
                    Icon(
                        Icons.Filled.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (controller.shuffleModeEnabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }

                // Repeat
                IconButton(onClick = {
                    val newMode = when (controller.repeatMode) {
                        Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
                        Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
                        else -> Player.REPEAT_MODE_OFF
                    }
                    controller.repeatMode = newMode
                }) {
                    val icon = when (controller.repeatMode) {
                        Player.REPEAT_MODE_ONE -> Icons.Filled.RepeatOne
                        Player.REPEAT_MODE_ALL -> Icons.Filled.Repeat
                        else -> Icons.Filled.Repeat
                    }
                    Icon(
                        icon,
                        contentDescription = "Repeat",
                        tint = if (controller.repeatMode != Player.REPEAT_MODE_OFF)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

        }
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
