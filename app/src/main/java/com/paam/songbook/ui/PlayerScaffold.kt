package com.paam.songbook.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.paam.songbook.model.Song
import com.paam.songbook.model.sampleSongs
import com.paam.songbook.ui.components.AlbumCard
import com.paam.songbook.ui.components.GroupedList
import com.paam.songbook.ui.components.SongList

/**
 * Root scaffold for the app.
 * Hosts HomeScreen + UnifiedPlayer (mini/full) in a BottomSheetScaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScaffold(controller: MediaController) {
    val songs = remember { sampleSongs() }
    var isExpanded by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        sheetContent = {
            UnifiedPlayer(
                controller = controller,
                isExpanded = isExpanded,
               // onCollapse = { isExpanded = false }
            )
        },
        sheetPeekHeight = 72.dp // mini-player height
    ) { padding ->
        HomeScreen(
            songs = songs,
            onSongSelected = { song ->
                val mediaItem = MediaItem.Builder()
                    .setUri(song.url)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(song.title)
                            .setArtist(song.artist)
                            .setArtworkUri(Uri.parse(song.albumArt))
                            .build()
                    )
                    .build()

                try {
                    controller.setMediaItem(mediaItem)
                    controller.prepare()
                    controller.play()
                    isExpanded = true
                } catch (e: Exception) {
                    // TODO: log or show error UI
                }
            },
            modifier = Modifier.padding(padding)
        )
    }
}

/**
 * Home screen with carousel, tabs, and song list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Artists", "Folders")

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("My Music") },
                actions = {
                    IconButton(onClick = { /* TODO: search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Carousel
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(songs.take(10)) { song ->
                    AlbumCard(song = song, onClick = { onSongSelected(song) })
                }
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Song list
            when (tabs[selectedTab]) {
                "All" -> SongList(songs, onSongSelected)
                "Artists" -> GroupedList(songs.groupBy { it.artist }, onSongSelected)
                "Folders" -> GroupedList(songs.groupBy { extractFolder(it.url) }, onSongSelected)
            }
        }
    }
}

/**
 * Extract folder name from a file path or URI.
 */
fun extractFolder(path: String): String {
    val cleaned = path.replace("file://", "")
    val segments = cleaned.split('/', '\\').filter { it.isNotBlank() }
    return if (segments.size > 1) segments[segments.size - 2] else "Unknown"
}
