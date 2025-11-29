package com.paam.songbook.ui.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.paam.songbook.media.toMediaItem
import com.paam.songbook.model.Playlist
import com.paam.songbook.model.Song
import com.paam.songbook.ui.songs.SongListItem // 👈 It can now be imported

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    songs: List<Song>,
    navController: NavController,
    mediaController: MediaController
) {
    Scaffold(
        topBar = { /* ... same as before ... */ },
        containerColor = Color(0xFF0F2027)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- 1. COVER ART ---
            item {
                val errorPainter = rememberVectorPainter(image = Icons.Default.MusicNote)
                val imagePainter = rememberAsyncImagePainter(
                    model = playlist.coverArtUrl,
                    error = errorPainter,
                    fallback = errorPainter
                )
                Image(
                    painter = imagePainter,
                    contentDescription = playlist.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }

            // --- 2. DESCRIPTION ---
            if (!playlist.description.isNullOrBlank()) {
                item {
                    Text(
                        text = playlist.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }

            // --- 3. SEPARATOR ---
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                )
            }

            // --- 4. SONG LIST ---
            items(songs) { song ->
                SongListItem(
                    song = song,
                    isPlaying = mediaController.isPlaying && mediaController.currentMediaItem?.mediaId == song.songID.toString(),
                    onItemSelected = {
                        // This logic sets the media controller's queue to the list of songs in the playlist
                        val mediaItems = songs.map { it.toMediaItem() }
                        val startIndex = songs.indexOf(song).coerceAtLeast(0)
                        mediaController.setMediaItems(mediaItems, startIndex, 0L)
                        mediaController.prepare()
                        mediaController.play()
                    },
                    onPlayPauseClicked = {
                        if (mediaController.isPlaying && mediaController.currentMediaItem?.mediaId == song.songID.toString()) {
                            mediaController.pause()
                        } else {
                            // If it's a different song, start the whole playlist from that point
                            val mediaItems = songs.map { it.toMediaItem() }
                            val startIndex = songs.indexOf(song).coerceAtLeast(0)
                            mediaController.setMediaItems(mediaItems, startIndex, 0L)
                            mediaController.prepare()
                            mediaController.play()
                        }
                    },
                    onMoreClicked = { /* TODO */ }
                )
            }
        }
    }
}
