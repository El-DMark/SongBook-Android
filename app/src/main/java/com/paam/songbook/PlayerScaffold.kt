package com.paam.songbook.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.paam.songbook.ui.theme.UnifiedPlayer
// --- Song model ---

// --- Sample songs (replace later with MediaStore/backend) ---
//fun sampleSongs(): List<Song> = listOf(
//    Song("Moonlight", "Ludwig", "/storage/emulated/0/Music/moonlight.mp3", "file:///storage/emulated/0/Music/art/moonlight.jpg"),
//    Song("Aurora", "Nova", "/storage/emulated/0/Music/aurora.mp3", "file:///storage/emulated/0/Music/art/aurora.jpg"),
//    Song("Midnight Drive", "Neon", "/storage/emulated/0/Music/midnight.mp3", "file:///storage/emulated/0/Music/art/midnight.jpg"),
//    Song("Slow Waves", "Coast", "/storage/emulated/0/Music/slowwaves.mp3", "file:///storage/emulated/0/Music/art/slowwaves.jpg")
//)

// --- Root scaffold ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScaffold(controller: MediaController) {
    val songs = remember { sampleSongs() }
    var currentSong by remember { mutableStateOf<Song?>(null) }
    var isExpanded by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        sheetContent = {
            UnifiedPlayer(
                controller = controller,
                isExpanded = isExpanded,
                onCollapse = { isExpanded = false }
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

                controller.setMediaItem(mediaItem)
                controller.prepare()
                controller.play()

                currentSong = song
                isExpanded = true
            },
            modifier = Modifier.padding(padding)
        )
    }
}

// --- Home screen ---
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
                "Artists" -> {
                    val grouped = songs.groupBy { it.artist }
                    GroupedList(grouped, onSongSelected)
                }
                "Folders" -> {
                    val grouped = songs.groupBy { extractFolder(it.url) }
                    GroupedList(grouped, onSongSelected)
                }
            }
        }
    }
}

// --- UI helpers ---
@Composable
fun AlbumCard(song: Song, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(song.albumArt),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(6.dp))
        Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun SongList(songs: List<Song>, onSongSelected: (Song) -> Unit) {
    LazyColumn {
        items(songs) { song ->
            SongRow(song, onClick = { onSongSelected(song) })
        }
    }
}

@Composable
fun GroupedList(grouped: Map<String, List<Song>>, onSongSelected: (Song) -> Unit) {
    LazyColumn {
        grouped.forEach { (group, groupSongs) ->
            item {
                Text(
                    text = group,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
            items(groupSongs) { song ->
                SongRow(song, onClick = { onSongSelected(song) })
            }
        }
    }
}

@Composable
fun SongRow(song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(song.albumArt),
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(song.title, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

fun extractFolder(path: String): String {
    val cleaned = path.replace("file://", "")
    val segments = cleaned.split('/', '\\').filter { it.isNotBlank() }
    return if (segments.size > 1) segments[segments.size - 2] else "Unknown"
}
