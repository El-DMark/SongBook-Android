package com.paam.songbook.ui.artists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.media.toMediaItem
import com.paam.songbook.ui.songs.SongListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistName: String,
    allSongs: List<Song>,
    navController: NavController,
    mediaController: MediaController
) {
    // Filter the songs to get only those by the selected artist
    val songsByArtist = allSongs.filter { it.artist == artistName }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(artistName, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFF0F2027) // Use a consistent background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Reuse the existing SongListScreen to display the filtered songs
            SongListScreen(
                songs = songsByArtist,
                onSongSelected = { selectedSong ->
                    val mediaItems = songsByArtist.map { it.toMediaItem() }
                    val startIndex = songsByArtist.indexOf(selectedSong).coerceAtLeast(0)
                    mediaController.setMediaItems(mediaItems, startIndex, 0L)
                    mediaController.prepare()
                    mediaController.play()
                },
                controller = mediaController
            )
        }
    }
}
