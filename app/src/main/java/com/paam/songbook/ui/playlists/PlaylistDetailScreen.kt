package com.paam.songbook.ui.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.paam.songbook.data.DownloadUtil
import com.paam.songbook.data.local.dao.entity.FavoriteSong
import com.paam.songbook.media.toMediaItem
import com.paam.songbook.model.Playlist
import com.paam.songbook.model.Song
import com.paam.songbook.ui.songs.SongListItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    songs: List<Song>,
    favs: List<FavoriteSong>,
    navController: NavController,
    mediaController: MediaController,
    onToggleFavorite: (Song) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🔹 LIVE STATE OBSERVATION (Add this part)
    var globallyPlaying by remember { mutableStateOf(mediaController.isPlaying) }
    var currentMediaId by remember { mutableStateOf(mediaController.currentMediaItem?.mediaId) }

    DisposableEffect(mediaController) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                globallyPlaying = isPlaying
            }
            override fun onMediaItemTransition(item: androidx.media3.common.MediaItem?, reason: Int) {
                currentMediaId = item?.mediaId
            }
        }
        mediaController.addListener(listener)
        onDispose { mediaController.removeListener(listener) }
    }

    val favoriteIds = remember(favs) {
        favs.map { it.songId.toLong() }.toSet()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlist.name, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF0F2027)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            item {
                val errorPainter = rememberVectorPainter(image = Icons.Default.MusicNote)
                Image(
                    painter = rememberAsyncImagePainter(model = playlist.coverArtUrl, error = errorPainter),
                    contentDescription = playlist.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                )
            }

            // ... (Description and Divider items remain same)

            items(items = songs, key = { it.songID }) { song ->
                val isFavorite = favoriteIds.contains(song.songID.toLong())

                // 🔹 Match live state
                val isThisSongPlaying = globallyPlaying && currentMediaId == song.songID.toString()

                SongListItem(
                    song = song,
                    isFavorite = isFavorite,
                    onFavoriteToggle = { onToggleFavorite(song) },
                    isPlaying = isThisSongPlaying,
                    onItemSelected = {
                        val mediaItems = songs.map { it.toMediaItem() }
                        val startIndex = songs.indexOf(song).coerceAtLeast(0)
                        mediaController.setMediaItems(mediaItems, startIndex, 0L)
                        mediaController.prepare()
                        mediaController.play()
                    },
                    onPlayPauseClicked = {
                        if (isThisSongPlaying) {
                            mediaController.pause()
                        } else {
                            if (currentMediaId == song.songID.toString()) {
                                mediaController.play()
                            } else {
                                val mediaItems = songs.map { it.toMediaItem() }
                                mediaController.setMediaItems(mediaItems, songs.indexOf(song).coerceAtLeast(0), 0L)
                                mediaController.prepare()
                                mediaController.play()
                            }
                        }
                    },
                    onDownloadToggle = { isCurrentlyDownloaded ->
                        scope.launch {
                            if (isCurrentlyDownloaded) DownloadUtil.removeDownload(context, song.url)
                            else DownloadUtil.downloadMedia(context, song.url)
                        }
                    }
                )
            }
        }
    }
}