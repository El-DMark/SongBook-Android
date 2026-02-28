package com.paam.songbook.ui.fullplayer

import Lyrics
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.paam.songbook.data.DownloadUtil
import com.paam.songbook.model.Song
import kotlinx.coroutines.delay
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
    isFavorite: Boolean,
    onToggleFavorite: (Boolean) -> Unit,
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

    // 🔹 Download States
    var isDownloaded by remember(currentSong.songID) { mutableStateOf(false) }
    var isDownloading by remember(currentSong.songID) { mutableStateOf(false) }
    var downloadProgress by remember(currentSong.songID) { mutableStateOf(0f) }

    // 🔹 1. Persistence Check: Runs when song loads
    LaunchedEffect(currentSong.songID) {
        if (!currentSong.url.isNullOrBlank()) {
            isDownloaded = DownloadUtil.isDownloaded(context, currentSong.url)
        }
    }

    // 🔹 2. Live Update Observer: Forces UI refresh when download finishes
    LaunchedEffect(isDownloading) {
        if (!isDownloading && !currentSong.url.isNullOrBlank()) {
            // Small delay to allow Media3 cache index to finalize
           // delay(500)
            isDownloaded = DownloadUtil.isDownloaded(context, currentSong.url)
        }
    }

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

    val foregroundColor = if (dominantColor.isDark()) Color.White else Color.Black

    var sliderPosition by remember { mutableStateOf(0f) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var previewTime by remember { mutableStateOf<Long?>(null) }
    var showLyrics by remember { mutableStateOf(false) }
    var fetchedLyrics by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val transition = updateTransition(currentSong.title, label = "SongChange")

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

    LaunchedEffect(currentSong.songID) {
        if (showLyrics && matchedLyrics != null) {
            fetchedLyrics = buildString {
                appendLine(matchedLyrics.chorus)
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
        SubcomposeAsyncImage(
            model = currentSong.albumArt,
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .blur(24.dp),
            contentScale = ContentScale.Crop
        )

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
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
                                appendLine(matchedLyrics.chorus)
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
                    albumAlpha = albumAlpha
                )
            }

            Spacer(Modifier.height(30.dp))

            // 🔹 ICONS AND SONG INFO ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Favorite Button (Left)
                IconButton(onClick = { onToggleFavorite(isFavorite) }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFE91E63) else foregroundColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // 2. Song Info (Center - Flexible)
                Box(modifier = Modifier.weight(1f)) {
                    SongInfoSection(
                        title = currentSong.title,
                        artist = currentSong.artist,
                        textColor = foregroundColor,
                        isDownloaded = isDownloaded,
                        isDownloading = isDownloading,
                        progress = downloadProgress,
                        onDownloadClick = {
                            coroutineScope.launch {
                                if (currentSong.url.isNullOrBlank() || isDownloading) return@launch

                                if (isDownloaded) {
                                    // 🔹 1. REMOVE DOWNLOAD
                                    DownloadUtil.removeDownload(context, currentSong.url)
                                    // Small delay to let file system reflect change
                                    delay(300)
                                    isDownloaded = false
                                } else {
                                    // 🔹 2. START DOWNLOAD
                                    isDownloading = true
                                    downloadProgress = 0f

                                    DownloadUtil.downloadMedia(context, currentSong.url) { progress ->
                                        downloadProgress = (progress / 100f).toFloat()
                                    }

                                    // Optional: Wait a moment at 100% so the user sees completion
                                    //delay(500)

                                    // Verify status before flipping the UI
                                    val verified = DownloadUtil.isDownloaded(context, currentSong.url)
                                    isDownloaded = verified
                                    isDownloading = false
                                }
                            }
                        }
                    )
                }
            }
            Spacer(Modifier.height(5.dp))

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
