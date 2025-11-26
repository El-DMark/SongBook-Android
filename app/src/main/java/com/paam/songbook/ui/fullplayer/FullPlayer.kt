package com.paam.songbook.ui.fullplayer

import Lyrics
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil.compose.SubcomposeAsyncImage
import coil.ImageLoader
import coil.request.ImageRequest
import com.paam.songbook.Model.Song
import kotlinx.coroutines.launch

// ✅ Helper to check brightness
fun Color.isDark(): Boolean {
    val darkness = 1 - (0.299 * red + 0.587 * green + 0.114 * blue)
    return darkness >= 0.5
}

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
    lyrics: List<Lyrics>
) {
    if (currentSong == null) return

    val context = LocalContext.current
    val surfaceColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember { mutableStateOf(surfaceColor) }

    // Extract dominant color from album art
    LaunchedEffect(currentSong?.albumArt) {
        val request = ImageRequest.Builder(context)
            .data(currentSong.albumArt)
            .allowHardware(false)
            .build()

        val result = ImageLoader(context).execute(request)
        val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
        bitmap?.let {
            Palette.from(it).generate { palette ->
                palette?.dominantSwatch?.rgb?.let { colorInt ->
                    dominantColor = Color(colorInt)
                }
            }
        }
    }

    // ✅ Adaptive foreground tint
    val foregroundColor = if (dominantColor.isDark()) Color.White else Color.Black

    var sliderPosition by remember { mutableStateOf(0f) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var previewTime by remember { mutableStateOf<Long?>(null) }
    var showLyrics by remember { mutableStateOf(false) }
    var fetchedLyrics by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
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

    // ✅ Auto-refresh lyrics when song changes
    LaunchedEffect(currentSong.songID) {
        if (showLyrics && matchedLyrics != null) {
            fetchedLyrics = buildString {
                appendLine("Chorus:\n${matchedLyrics.chorus}")
                matchedLyrics.verses.forEach { appendLine(it).appendLine() }
            }
        } else {
            fetchedLyrics = null
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
        // 🔹 Blurred album art background
        SubcomposeAsyncImage(
            model = currentSong.albumArt,
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .blur(24.dp),
            contentScale = ContentScale.Crop
        )

        // 🔹 Gradient overlay using dominant color
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            dominantColor.copy(alpha = 0.95f),
                            dominantColor.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        // 🔹 Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerHeaderBar(
                onCollapse = onCollapse,
                showLyrics = showLyrics,
                matchedLyricsAvailable = matchedLyrics != null,
                onToggleLyrics = {
                    showLyrics = !showLyrics
                    if (showLyrics && matchedLyrics != null) {
                        coroutineScope.launch {
                            fetchedLyrics = buildString {
                                appendLine("Chorus:\n${matchedLyrics.chorus}")
                                matchedLyrics.verses.forEach { appendLine(it).appendLine() }
                            }
                        }
                    }
                },
                tint = foregroundColor
            )

            Spacer(Modifier.height(16.dp))

            if (showLyrics && fetchedLyrics != null) {
                LyricsDisplay(fetchedLyrics, textColor = foregroundColor)
            } else {
                AlbumArtDisplay(
                    albumArt = currentSong.albumArt,
                    title = currentSong.title,
                   // albumSize = albumSize,
                    albumAlpha = albumAlpha
                )
            }

            Spacer(Modifier.height(30.dp))

            SongInfoSection(
                title = currentSong.title,
                artist = currentSong.artist,
                textColor = foregroundColor
            )

            Spacer(Modifier.height(15.dp))

            SeekBarWithPreview(
                position = position,
                duration = duration,
                sliderPosition = sliderPosition,
                onSliderChange = { sliderPosition = it; isUserSeeking = true },
                onSeekFinished = {
                    onSeek((sliderPosition * duration).toLong())
                    isUserSeeking = false
                },
                previewTime = previewTime,
                onPreviewTimeChange = { previewTime = it },
                tint = foregroundColor
            )

            PlaybackControls(
                isPlaying = isPlaying,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                tint = foregroundColor
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}
