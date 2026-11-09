package com.paam.songbook.ui.songs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
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
import com.paam.songbook.model.Playlist
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SongListScreen(
    songs: List<Song>,
    playlists: List<Playlist> = emptyList(),
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

    // 🔹 STATE
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedSaint by remember { mutableStateOf<String?>(null) }
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }

    val filteredSongs = remember(songs, searchQuery, selectedFilter, selectedSaint, selectedPlaylist) {
        var result = songs

        if (searchQuery.isNotBlank()) {
            result = result.filter { 
                it.title.contains(searchQuery, ignoreCase = true) || 
                it.artist.contains(searchQuery, ignoreCase = true) 
            }
        }

        if (selectedFilter == "Saints" && selectedSaint != null) {
            result = result.filter { it.artist == selectedSaint }
        } else if (selectedFilter == "Playlists" && selectedPlaylist != null) {
            result = result.filter { selectedPlaylist!!.songs.contains(it.songID) }
        }

        result
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Keep the header always visible at the top
            Box(modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)) {
                DiscoverHeader(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedFilter = selectedFilter,
                    onFilterChange = { newFilter -> 
                        selectedFilter = newFilter 
                        selectedSaint = null
                        selectedPlaylist = null
                    }
                )
            }
            
            Box(modifier = Modifier.weight(1f)) {
                if (searchQuery.isNotEmpty()) {
                    // --- SEARCH RESULTS ---
                    SongsListView(filteredSongs, favoriteIds, globallyPlaying, currentMediaId, controller, onSongSelected, onToggleFavorite, bottomInset, scope, context)
                } else {
                    when (selectedFilter) {
                        "Hymns" -> {
                            SongsListView(filteredSongs, favoriteIds, globallyPlaying, currentMediaId, controller, onSongSelected, onToggleFavorite, bottomInset, scope, context)
                        }
                        "Saints" -> {
                            if (selectedSaint == null) {
                                val uniqueSaints = remember(songs) { songs.map { it.artist }.distinct() }
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(bottom = bottomInset + 100.dp, start = 16.dp, end = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(uniqueSaints) { saint ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth().aspectRatio(0.85f).clickable { selectedSaint = saint },
                                            shape = RoundedCornerShape(24.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                        ) {
                                            val saintHash = saint.hashCode() and 0x7FFFFFFF
                                            val gradientColors = listOf(
                                                listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)), // Purple
                                                listOf(Color(0xFFFF416C), Color(0xFFFF4B2B)), // Red/Orange
                                                listOf(Color(0xFF00B4DB), Color(0xFF0083B0)), // Blue
                                                listOf(Color(0xFFF7971E), Color(0xFFFFD200)), // Yellow/Gold
                                                listOf(Color(0xFF11998E), Color(0xFF38EF7D)), // Green
                                                listOf(Color(0xFFDA22FF), Color(0xFF9733EE))  // Pink/Purple
                                            )[saintHash % 6]
                                            
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Brush.linearGradient(gradientColors)), 
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(72.dp)
                                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                                            .background(Color.White.copy(alpha = 0.2f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = saint.firstOrNull()?.uppercase() ?: "?",
                                                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Black),
                                                            color = Color.White
                                                        )
                                                    }
                                                    Spacer(Modifier.height(16.dp))
                                                    Text(
                                                        text = saint,
                                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                                                        color = Color.White,
                                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                        maxLines = 2,
                                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                SongsListView(filteredSongs, favoriteIds, globallyPlaying, currentMediaId, controller, onSongSelected, onToggleFavorite, bottomInset, scope, context)
                            }
                        }
                        "Playlists" -> {
                            if (selectedPlaylist == null) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(bottom = bottomInset + 100.dp, start = 16.dp, end = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(playlists) { playlist ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable { selectedPlaylist = playlist },
                                            shape = RoundedCornerShape(24.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                if (playlist.coverArtUrl != null) {
                                                    Image(
                                                        painter = coil.compose.rememberAsyncImagePainter(playlist.coverArtUrl),
                                                        contentDescription = playlist.name,
                                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                    Box(modifier = Modifier.fillMaxSize().background(
                                                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)))
                                                    ))
                                                } else {
                                                    Box(modifier = Modifier.fillMaxSize().background(
                                                        Brush.linearGradient(listOf(Color(0xFF2C3E50), Color(0xFF000000)))
                                                    ))
                                                }
                                                Text(
                                                    text = playlist.name,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                                                    color = Color.White,
                                                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                                                    maxLines = 2,
                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                SongsListView(filteredSongs, favoriteIds, globallyPlaying, currentMediaId, controller, onSongSelected, onToggleFavorite, bottomInset, scope, context)
                            }
                        }
                        else -> {
                            // --- DISCOVER LAYOUT ---
                            androidx.compose.foundation.lazy.LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = bottomInset + 100.dp),
                                verticalArrangement = Arrangement.spacedBy(32.dp)
                            ) {
                                item {
                                    if (songs.isNotEmpty()) {
                                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
//                                                Text(
//                                                    "Made for this moment",
//                                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
//                                                    color = Color.White,
//                                                    modifier = Modifier.weight(1f)
//                                                )
//                                                Text("See all", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Spacer(Modifier.height(16.dp))
                                            FeaturedSongCard(songs = songs, controller = controller)
                                        }
                                    }
                                }

                                item {
                                    PlaylistGridSection(
                                        playlists = playlists,
                                        onPlaylistClick = { playlist ->
                                            selectedPlaylist = playlist
                                            selectedFilter = "Playlists"
                                        }
                                    )
                                }

                                item {
                                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Trending now", style = MaterialTheme.typography.titleLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = Color.White)
                                                Spacer(Modifier.height(4.dp))
                                                Text("The songs everyone is worshipping to.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text(
                                                "View All",
                                                style = MaterialTheme.typography.labelSmall, 
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.clickable { selectedFilter = "Hymns" }.padding(4.dp)
                                            )
                                        }
                                    }
                                }

                                // Wrap trending list in a card-like shape
                                item {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(24.dp))
                                            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                            .padding(vertical = 8.dp)
                                    ) {
                                        songs.take(10).forEachIndexed { index, song ->
                                            val isFavorite = favoriteIds.contains(song.songID.toLong())
                                            val isThisSongPlaying = globallyPlaying && currentMediaId == song.songID.toString()

                                            SongListItem(
                                                song = song,
                                                index = index + 1,
                                                isFavorite = isFavorite,
                                                isPlaying = isThisSongPlaying,
                                                onPlayPauseClicked = {
                                                    if (isThisSongPlaying) {
                                                        controller.pause()
                                                    } else {
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
                                            
                                            if (index < minOf(songs.size, 10) - 1) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(horizontal = 16.dp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SongsListView(
    songs: List<Song>,
    favoriteIds: Set<Long>,
    globallyPlaying: Boolean,
    currentMediaId: String?,
    controller: MediaController,
    onSongSelected: (Song) -> Unit,
    onToggleFavorite: (Song) -> Unit,
    bottomInset: androidx.compose.ui.unit.Dp,
    scope: kotlinx.coroutines.CoroutineScope,
    context: android.content.Context
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomInset + 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            items = songs,
            key = { _, song -> song.songID }
        ) { index, song ->
            val isFavorite = favoriteIds.contains(song.songID.toLong())
            val isThisSongPlaying = globallyPlaying && currentMediaId == song.songID.toString()

            SongListItem(
                song = song,
                index = index + 1,
                isFavorite = isFavorite,
                isPlaying = isThisSongPlaying,
                onPlayPauseClicked = {
                    if (isThisSongPlaying) {
                        controller.pause()
                    } else {
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

@Composable
private fun DiscoverHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        //Text("Discover", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold), color = Color.White)
        //Spacer(Modifier.height(8.dp))
       // Text("Music for every season of faith.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
       // Spacer(Modifier.height(24.dp))

        // Search Bar (Now Functional)
        androidx.compose.foundation.text.BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.2f))
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text("What do you want to listen to?", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        innerTextField()
                    }
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.clickable { onSearchQueryChange("") }
                        )
                    }
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        // Filter Chips (Now Functional)
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val chips = listOf("All", "Hymns", "Saints", "Playlists")
            itemsIndexed(chips) { index, chip ->
                val isSelected = chip == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) Color.White else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onFilterChange(chip) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = chip,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = if (isSelected) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistGridSection(playlists: List<Playlist>, onPlaylistClick: (Playlist) -> Unit) {
    val topPlaylists = playlists.take(4)
    val colors = listOf(
        Color(0xFF0F3B4A),
        Color(0xFF332A5A),
        Color(0xFF4A4026),
        Color(0xFF4A2635)
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (topPlaylists.size > 0) PlaylistCard(topPlaylists[0], colors[0], modifier = Modifier.weight(1f).clickable { onPlaylistClick(topPlaylists[0]) })
            if (topPlaylists.size > 1) PlaylistCard(topPlaylists[1], colors[1], modifier = Modifier.weight(1f).clickable { onPlaylistClick(topPlaylists[1]) })
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (topPlaylists.size > 2) PlaylistCard(topPlaylists[2], colors[2], modifier = Modifier.weight(1f).clickable { onPlaylistClick(topPlaylists[2]) })
            if (topPlaylists.size > 3) PlaylistCard(topPlaylists[3], colors[3], modifier = Modifier.weight(1f).clickable { onPlaylistClick(topPlaylists[3]) })
        }
    }
}

@Composable
private fun PlaylistCard(playlist: Playlist, backgroundColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
    ) {
        // Full bleed background image
        if (playlist.coverArtUrl != null) {
            coil.compose.AsyncImage(
                model = playlist.coverArtUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            // Gradient overlay to make text readable
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)))
            ))
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = playlist.name, 
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), 
                color = Color.White,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${playlist.songs.size} songs", 
                style = MaterialTheme.typography.labelMedium, 
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListItem(
    song: Song,
    index: Int? = null,
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isDownloaded by remember(song.songID) {
        mutableStateOf(DownloadUtil.isDownloaded(context, song.url))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemSelected() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Subtle Number Prefix
        if (index != null) {
            Text(
                text = index.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.width(32.dp)
            )
        }

        // Album Art
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = rememberAsyncImagePainter(song.albumArt),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Optional subtle indicator if downloaded
            if (isDownloaded) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Titles
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Play Icon (Sleek Outline style matching sample)
            IconButton(
                onClick = onPlayPauseClicked,
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // More Options Menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // The new Bottom Sheet
                if (showMenu) {
                    ModalBottomSheet(
                        onDismissRequest = { showMenu = false },
                        sheetState = sheetState,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        dragHandle = { BottomSheetDefaults.DragHandle() }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                        ) {
                            // Header: Album Art and Title
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(song.albumArt),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = song.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = song.artist,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 24.dp))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Action: Download
                            BottomSheetActionRow(
                                icon = if (isDownloaded) Icons.Default.Delete else Icons.Outlined.Download,
                                text = if (isDownloaded) "Remove Offline" else "Download Offline",
                                tint = if (isDownloaded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
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

                            // Action: Favorite
                            BottomSheetActionRow(
                                icon = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                text = if (isFavorite) "Remove from Inspiration" else "Add to Inspiration",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                onClick = {
                                    showMenu = false
                                    onFavoriteToggle()
                                }
                            )

                            // Action: Share
                            BottomSheetActionRow(
                                icon = Icons.Default.Share,
                                text = "Share Song",
                                tint = MaterialTheme.colorScheme.onSurface,
                                onClick = {
                                    showMenu = false
                                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(android.content.Intent.EXTRA_TEXT, "Listen to ${song.title} by ${song.artist} on Tehillah! https://tehillah.live/song/${song.songID}")
                                    }
                                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Song"))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomSheetActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = tint,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )
    }
}