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
import com.paam.songbook.model.toMediaItem
import com.paam.songbook.ui.components.AlbumCard
import com.paam.songbook.ui.components.GroupedList
import com.paam.songbook.ui.components.SongList

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
                songs = songs
            )
        },
        sheetPeekHeight = 72.dp
    ) { padding ->
        HomeScreen(
            songs = songs,
            onSongSelected = { song ->
                // Build full playlist once, jump to selected index
                val mediaItems = songs.map { it.toMediaItem() }
                val startIndex = songs.indexOf(song).coerceAtLeast(0)

                try {
                    controller.setMediaItems(mediaItems, startIndex, /*startPositionMs=*/0L)
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
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(songs.take(10)) { song ->
                    AlbumCard(song = song, onClick = { onSongSelected(song) })
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (tabs[selectedTab]) {
                "All" -> SongList(songs, onSongSelected)
                "Artists" -> GroupedList(songs.groupBy { it.artist }, onSongSelected)
                "Folders" -> GroupedList(songs.groupBy { extractFolder(it.url) }, onSongSelected)
            }
        }
    }
}

fun extractFolder(path: String): String {
    val cleaned = path.replace("file://", "")
    val segments = cleaned.split('/', '\\').filter { it.isNotBlank() }
    return if (segments.size > 1) segments[segments.size - 2] else "Unknown"
}
