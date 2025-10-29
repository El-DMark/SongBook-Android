package com.paam.songbook.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.media.toMediaItem
import com.paam.songbook.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    songs: List<Song>,
    navController: NavController,
    controller: MediaController,
    query: String,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        // 🔹 Carousel
        NewlyAddedCarousel(
            songs = songs,
            onSongSelected = { selectedSong ->
                val mediaItems = songs.map { it.toMediaItem() }
                val startIndex = songs.indexOfFirst { it.songID == selectedSong.songID }
                if (startIndex != -1) {
                    controller.setMediaItems(mediaItems, startIndex, 0L)
                    controller.prepare()
                    controller.play()
                }
            }
        )

        // 🔹 Language filter row (direct, no pager)
        LanguageFilterRow(
            languages = listOf("Hindi", "English"),
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { selectedLanguage = it }
        )

        // 🔹 Apply filters + search
        val filteredSongs = songs.filter {
            (selectedLanguage == null || it.Language == selectedLanguage) &&
                    (query.isBlank() ||
                            it.title.contains(query, ignoreCase = true) ||
                            it.artist.contains(query, ignoreCase = true))
        }

        // 🔹 Song list
        SongList(
            songs = filteredSongs,
            onSongSelected = { selectedSong ->
                val mediaItems = songs.map { it.toMediaItem() }
                val startIndex = songs.indexOfFirst { it.songID == selectedSong.songID }
                if (startIndex != -1) {
                    controller.setMediaItems(mediaItems, startIndex, 0L)
                    controller.prepare()
                    controller.play()
                }
            },
            isLoading = songs.isEmpty(),
            modifier = Modifier.fillMaxSize()
        )
    }
}
