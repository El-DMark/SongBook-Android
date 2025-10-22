package com.paam.songbook.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.paam.songbook.model.Song
import com.valentinilk.shimmer.shimmer

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun FullPlayer(
    currentSong: Song?,
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    onCollapse: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit
) {
    if (currentSong == null) return

    var sliderPosition by remember { mutableStateOf(0f) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var previewTime by remember { mutableStateOf<Long?>(null) }
    val density = LocalDensity.current

    // Keep slider in sync with playback unless user is dragging
    LaunchedEffect(position, duration, isUserSeeking) {
        if (!isUserSeeking && duration > 0) {
            sliderPosition = position / duration.toFloat()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Blurred background with fallback + shimmer
        SubcomposeAsyncImage(
            model = currentSong.albumArt,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(40.dp),
            contentScale = ContentScale.Crop,
            alpha = 0.5f,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmer()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            },
            error = {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Fallback",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Collapse button
            IconButton(onClick = onCollapse, modifier = Modifier.align(Alignment.Start)) {
                Icon(Icons.Default.ExpandMore, contentDescription = "Collapse")
            }

            Spacer(Modifier.height(16.dp))

            // Album art with shimmer + fallback
            SubcomposeAsyncImage(
                model = currentSong.albumArt,
                contentDescription = currentSong.title,
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmer()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                },
                error = {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Fallback",
                        modifier = Modifier.size(96.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            Spacer(Modifier.height(24.dp))

            // Title + artist
            Text(currentSong.title, style = MaterialTheme.typography.titleLarge)
            Text(
                currentSong.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // Slider with preview bubble
            // Progress bar with preview bubble
            // State only for user interaction
            var sliderPosition by remember { mutableStateOf(0f) }
            var isUserSeeking by remember { mutableStateOf(false) }
            var previewTime by remember { mutableStateOf<Long?>(null) }

            val playbackFraction = if (duration > 0) position / duration.toFloat() else 0f

// The value shown on the slider: playback when not seeking, user drag when seeking
            val sliderValue = if (isUserSeeking) sliderPosition else playbackFraction

// Animate changes so skips/jumps are smooth
            val animatedSliderValue by animateFloatAsState(
                targetValue = sliderValue,
                animationSpec = tween(durationMillis = 300), // adjust speed if needed
                label = "SliderAnim"
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val maxWidthPx = with(density) { this@BoxWithConstraints.maxWidth.toPx() }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Crossfade(targetState = previewTime, label = "PreviewBubble") { time ->
                        if (time != null && duration > 0) {
                            val offsetPercent = animatedSliderValue.coerceIn(0f, 1f)
                            val rawOffset = offsetPercent * (maxWidthPx - 32)
                            val offsetDp = with(density) { rawOffset.toDp() }
                            val clampedOffset = offsetDp.coerceIn(
                                0.dp,
                                this@BoxWithConstraints.maxWidth - 40.dp
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                // Vertical scrubbing line
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(x = clampedOffset)
                                        .width(2.dp)
                                        .height(24.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )

                                // Floating bubble
                                Text(
                                    text = formatTime(time),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(x = clampedOffset - 20.dp, y = (-28).dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Slider(
                        value = animatedSliderValue,
                        onValueChange = { newValue ->
                            sliderPosition = newValue
                            isUserSeeking = true
                            previewTime = (newValue * duration).toLong()
                        },
                        onValueChangeFinished = {
                            val seekPos = (sliderPosition * duration).toLong()
                            onSeek(seekPos)
                            isUserSeeking = false
                            previewTime = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }



            Spacer(Modifier.height(8.dp))

            // Elapsed / total time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(position), style = MaterialTheme.typography.bodySmall)
                Text(formatTime(duration), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(24.dp))

            // Playback controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                }
                IconButton(onClick = onPlayPause, modifier = Modifier.size(72.dp)) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(48.dp)
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next")
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
