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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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

// Replace with your real Song model and loader.
// For now, keep this simple placeholder to make the screen runnable.
data class Song(
    val title: String,
    val artist: String,
    val url: String,         // local file path or http url
    val albumArt: String     // uri/string
)

// TODO: Replace this with MediaStore loader later or backend repository.
fun sampleSongs(): List<Song> = listOf(
    Song("Moonlight", "Ludwig", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "file:///storage/emulated/0/Music/art/moonlight.jpg"),
    Song("Aurora", "Nova", "/storage/emulated/0/Music/aurora.mp3", "file:///storage/emulated/0/Music/art/aurora.jpg"),
    Song("Midnight Drive", "Neon", "/storage/emulated/0/Music/midnight.mp3", "file:///storage/emulated/0/Music/art/midnight.jpg"),
    Song("Slow Waves", "Coast", "/storage/emulated/0/Music/slowwaves.mp3", "file:///storage/emulated/0/Music/art/slowwaves.jpg")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongBookAppUI(
    controller: MediaController,
    modifier: Modifier = Modifier
) {
    var currentSong by remember { mutableStateOf<Song?>(null) }
    val songs = remember { sampleSongs() }

    // Controls expansion of the player. Mini when false, full screen when true.
    var playerExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("My Music") },
                actions = {
                    IconButton(onClick = { /* TODO: search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* TODO: filters dialog */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filters")
                    }
                }
            )
        },
        // Sticky mini-player; tap to expand
        bottomBar = {
            UnifiedPlayer(
                controller = controller,
                isExpanded = playerExpanded,
                onCollapse = { playerExpanded = false }
            )
        },
        modifier = modifier
    ) { padding ->
        // Home content
        HomeContent(
            songs = songs,
            onSongSelected = { song ->
                // Build media item with metadata
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
                // Open the mini-player (collapsed at bottom). User can tap to expand.
                playerExpanded = false
            },
            onMiniPlayerTap = { playerExpanded = true },
            contentPadding = padding
        )
    }
}

@Composable
private fun HomeContent(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    onMiniPlayerTap: () -> Unit,
    contentPadding: PaddingValues
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Artists", "Folders")

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        // Carousel row (recent albums/songs)
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

        // List area
        when (tabs[selectedTab]) {
            "All" -> SongList(songs, onSongSelected)
            "Artists" -> {
                val grouped = remember(songs) { songs.groupBy { it.artist } }
                GroupedList(
                    grouped = grouped,
                    onSongSelected = onSongSelected
                )
            }
            "Folders" -> {
                val grouped = remember(songs) { songs.groupBy { extractFolder(it.url) } }
                GroupedList(
                    grouped = grouped,
                    onSongSelected = onSongSelected
                )
            }
        }
    }
}

@Composable
private fun AlbumCard(song: Song, onClick: () -> Unit) {
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
        Text(
            song.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SongList(songs: List<Song>, onSongSelected: (Song) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(songs) { song ->
            SongRow(song = song, onClick = { onSongSelected(song) })
        }
    }
}

@Composable
private fun GroupedList(
    grouped: Map<String, List<Song>>,
    onSongSelected: (Song) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        grouped.forEach { (groupTitle, groupSongs) ->
            item {
                Text(
                    text = groupTitle,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(groupSongs) { song ->
                SongRow(song = song, onClick = { onSongSelected(song) })
            }
        }
    }
}

@Composable
private fun SongRow(song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
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
        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                song.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun extractFolder(path: String): String {
    // Supports both file:/// and /storage/... patterns and http urls (best-effort)
    val cleaned = path.replace("file://", "")
    val segments = cleaned.split('/', '\\').filter { it.isNotBlank() }
    return if (segments.size > 1) segments[segments.size - 2] else "Unknown"
}
