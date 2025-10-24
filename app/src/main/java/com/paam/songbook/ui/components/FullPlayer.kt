package com.paam.songbook.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.paam.songbook.model.Song
import com.valentinilk.shimmer.shimmer

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
    var showLyrics by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val transition = updateTransition(currentSong.title, label = "SongChange")

    val albumSize by transition.animateDp(
        transitionSpec = { tween(500) },
        label = "AlbumSize"
    ) { if (it.isNotEmpty()) 300.dp else 0.dp }

    val albumAlpha by transition.animateFloat(
        transitionSpec = { tween(500) },
        label = "AlbumAlpha"
    ) { if (it.isNotEmpty()) 1f else 0f }

    LaunchedEffect(position, duration, isUserSeeking) {
        if (!isUserSeeking && duration > 0) {
            sliderPosition = position / duration.toFloat()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 20) onCollapse()
                }
            }
    ) {
        // Blurred background
        SubcomposeAsyncImage(
            model = currentSong.albumArt,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().blur(40.dp),
            contentScale = ContentScale.Crop,
            alpha = 0.5f,
            loading = { Box(Modifier.fillMaxSize().shimmer().background(MaterialTheme.colorScheme.surfaceVariant)) },
            error = {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Fallback",
                    modifier = Modifier.fillMaxSize().padding(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = onCollapse, modifier = Modifier.align(Alignment.Start)) {
                Icon(Icons.Default.ExpandMore, contentDescription = "Collapse")
            }

            Spacer(Modifier.height(16.dp))

            // Animated album art
            SubcomposeAsyncImage(
                model = currentSong.albumArt,
                contentDescription = currentSong.title,
                modifier = Modifier
                    .size(albumSize)
                    .alpha(albumAlpha)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .size(albumSize)
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

            Text(currentSong.title, style = MaterialTheme.typography.titleLarge)
            Text(currentSong.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(24.dp))

            // Slider with preview bubble
            val playbackFraction = if (duration > 0) position / duration.toFloat() else 0f
            val sliderValue = if (isUserSeeking) sliderPosition else playbackFraction
            val animatedSliderValue by animateFloatAsState(sliderValue, tween(300), label = "SliderAnim")

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val maxWidthPx = with(density) { maxWidth.toPx() }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Crossfade(targetState = previewTime, label = "PreviewBubble") { time ->
                        if (time != null && duration > 0) {
                            val offsetPercent = animatedSliderValue.coerceIn(0f, 1f)
                            val rawOffset = offsetPercent * (maxWidthPx - 32)
                            val offsetDp = with(density) { rawOffset.toDp() }
                            val clampedOffset = offsetDp.coerceIn(0.dp, this@BoxWithConstraints.maxWidth - 40.dp)

                            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(x = clampedOffset)
                                        .width(2.dp)
                                        .height(24.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = formatTime(time),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(x = clampedOffset - 20.dp, y = (-28).dp)
                                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Slider(
                        value = animatedSliderValue,
                        onValueChange = {
                            sliderPosition = it
                            isUserSeeking = true
                            previewTime = (it * duration).toLong()
                        },
                        onValueChangeFinished = {
                            onSeek((sliderPosition * duration).toLong())
                            isUserSeeking = false
                            previewTime = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatTime(position), style = MaterialTheme.typography.bodySmall)
                Text(formatTime(duration), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(24.dp))

            // Playback controls with buffering indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                }
                Box(modifier = Modifier.size(72.dp)) {
                    IconButton(onClick = onPlayPause, modifier = Modifier.size(72.dp)) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    // Optional: add buffering indicator here if needed
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Optional lyrics toggle
            if (!currentSong.lyrics.isNullOrBlank()) {
                TextButton(onClick = { showLyrics = !showLyrics }) {
                    Text(if (showLyrics) "Hide Lyrics" else "Show Lyrics")
                }
                AnimatedVisibility(showLyrics) {
                    Text(
                        text = currentSong.lyrics ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
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
