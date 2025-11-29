package com.paam.songbook.ui.components

import Lyrics
import android.annotation.SuppressLint
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
//import com.paam.songbook.model.Lyrics.LyricsRepository
import com.paam.songbook.model.Song
//import com.paam.songbook.model.LyricsRepository
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch

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
    onSeek: (Long) -> Unit,
    lyrics: List<Lyrics> // 🔹 passed from MainScaffold
) {
    if (currentSong == null) return

    var sliderPosition by remember { mutableStateOf(0f) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var previewTime by remember { mutableStateOf<Long?>(null) }
    var showLyrics by remember { mutableStateOf(false) }
    var fetchedLyrics by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
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

    val matchedLyrics = remember(currentSong.songID, lyrics) {
        lyrics.find { it.id == currentSong.songID }
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
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            ) {
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.Default.ExpandMore, contentDescription = "Collapse")
                }

                if (matchedLyrics != null) {
                    IconButton(
                        onClick = {
                            showLyrics = !showLyrics
                            if (showLyrics && fetchedLyrics == null) {
                                coroutineScope.launch {
                                    fetchedLyrics = buildString {
                                        matchedLyrics.verses.forEach { appendLine(it).appendLine() }
                                        appendLine("Chorus:\n${matchedLyrics.chorus}")
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = if (showLyrics) Icons.Default.Close else Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = if (showLyrics) "Hide Lyrics" else "Show Lyrics"
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (showLyrics && fetchedLyrics != null) {
                Text(
                    text = fetchedLyrics ?: "Loading lyrics...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            } else {
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
            }

            Spacer(Modifier.height(24.dp))


            Text(currentSong.title, style = MaterialTheme.typography.titleLarge)
            Text(currentSong.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(24.dp))

            val playbackFraction = if (duration > 0) position / duration.toFloat() else 0f
            val sliderValue = if (isUserSeeking) sliderPosition else playbackFraction
            val animatedSliderValue by animateFloatAsState(sliderValue, tween(300), label = "SliderAnim")

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val maxWidthPx = with(density) { maxWidth.toPx() }
                val maxWidthDp = maxWidth

                Box(modifier = Modifier.fillMaxWidth()) {
                    Crossfade(targetState = previewTime, label = "PreviewBubble") { time: Long? ->
                        if (time != null && duration > 0) {
                            val offsetPercent = animatedSliderValue.coerceIn(0f, 1f)
                            val rawOffset = offsetPercent * (maxWidthPx - 32)
                            val offsetDp = with(density) { rawOffset.toDp() }
                            val clampedOffset = offsetDp.coerceIn(0.dp, maxWidthDp - 40.dp)

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
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
