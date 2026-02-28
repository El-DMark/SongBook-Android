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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.paam.songbook.model.Song
import com.paam.songbook.data.DownloadUtil
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SongListScreen(
    songs: List<Song>,
    favoriteIds: Set<Long>,
    onSongSelected: (Song) -> Unit,
    onToggleFavorite: (Song) -> Unit,
    controller: MediaController
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🔹 OBSERVE PLAYBACK STATE LIVE
    var globallyPlaying by remember { mutableStateOf(controller.isPlaying) }
    var currentMediaId by remember { mutableStateOf(controller.currentMediaItem?.mediaId) }

    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                globallyPlaying = isPlaying
            }
            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                currentMediaId = mediaItem?.mediaId
            }
        }
        controller.addListener(listener)
        onDispose { controller.removeListener(listener) }
    }

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
                bottom = bottomInset + 72.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = songs,
                key = { song -> song.songID }
            ) { song ->
                val isFavorite = favoriteIds.contains(song.songID.toLong())

                // 🔹 Match live state against this specific song
                val isThisSongPlaying = globallyPlaying && currentMediaId == song.songID.toString()

                SongListItem(
                    song = song,
                    isFavorite = isFavorite,
                    isPlaying = isThisSongPlaying,
                    onPlayPauseClicked = {
                        if (isThisSongPlaying) {
                            controller.pause()
                        } else {
                            // If it's the same song but paused, just play
                            if (currentMediaId == song.songID.toString()) {
                                controller.play()
                            } else {
                                onSongSelected(song)
                            }
                        }
                    },
                    onFavoriteToggle = { onToggleFavorite(song) },
                    onDownloadToggle = { isCurrentlyDownloaded ->
                        scope.launch {
                            if (isCurrentlyDownloaded) {
                                DownloadUtil.removeDownload(context, song.url)
                            } else {
                                DownloadUtil.downloadMedia(context, song.url)
                            }
                        }
                    },
                    onItemSelected = { onSongSelected(song) }
                )
            }
        }
    }
}

@Composable
fun SongListItem(
    song: Song,
    isFavorite: Boolean,
    isPlaying: Boolean,
    onPlayPauseClicked: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDownloadToggle: (Boolean) -> Unit,
    onItemSelected: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    var isDownloaded by remember(song.songID) {
        mutableStateOf(DownloadUtil.isDownloaded(context, song.url))
    }

    val borderColor = if (isDownloaded) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.3f)
    val borderWidth = if (isDownloaded) 2.dp else 1.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemSelected() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = rememberAsyncImagePainter(song.albumArt),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

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

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPlayPauseClicked) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(Color(0xFF1E272C))
                        .width(190.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isDownloaded) "Remove Offline" else "Download",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isDownloaded) Icons.Default.Delete else Icons.Outlined.Download,
                                contentDescription = null,
                                tint = if (isDownloaded) Color(0xFFE57373) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            scope.launch {
                                val wasDownloaded = isDownloaded
                                onDownloadToggle(wasDownloaded)
                                if (!wasDownloaded) {
                                    repeat(40) {
                                        delay(500)
                                        if (DownloadUtil.isDownloaded(context, song.url)) {
                                            isDownloaded = true
                                            return@launch
                                        }
                                    }
                                } else {
                                    delay(300)
                                    isDownloaded = false
                                }
                            }
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isFavorite) "Remove" else "Add to Inspiration",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) Color(0xFFE91E63) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            onFavoriteToggle()
                        }
                    )
                }
            }
        }
    }
}