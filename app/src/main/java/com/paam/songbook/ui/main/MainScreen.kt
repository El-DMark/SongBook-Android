package com.paam.songbook.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.media.toMediaItem
import com.paam.songbook.ui.artists.ArtistListScreen
import com.paam.songbook.ui.components.ArtistFilterList
import com.paam.songbook.ui.home.HomeScreen
import com.paam.songbook.ui.playlists.PlaylistScreen
import com.paam.songbook.ui.songs.SongListScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    songs: List<Song>,
    navController: NavController,
    controller: MediaController
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    // --- NEW: STATE MANAGEMENT FOR BOTTOM SHEET ---
    // State to remember if the bottom sheet is open
    var isArtistFilterSheetOpen by remember { mutableStateOf(false) }
    // State for the bottom sheet itself
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    // --- END OF NEW CODE ---

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF2C5364))
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        if (isSearching) {
                            val focusRequester = remember { FocusRequester() }
                            LaunchedEffect(Unit) { focusRequester.requestFocus() }

                            TextField(
                                value = query,
                                onValueChange = { query = it },
                                placeholder = { Text("Search songs...", color = Color.LightGray) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    cursorColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                ),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        if (query.isNotEmpty()) query = "" else isSearching = false
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                                    }
                                }
                            )
                        } else {
                            Text("Songs of Bride", color = Color.White)
                        }
                    },
                    actions = {
                        if (!isSearching) {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                            }
                        }
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                HorizontalPager(
                    state = pagerState,
                    flingBehavior = PagerDefaults.flingBehavior(state = pagerState)
                ) { page ->
                    when (page) {
                        0 -> HomeScreen(
                            songs = songs,
                            navController = navController,
                            controller = controller
                        )
                        1 -> {
                            val filteredSongs = songs.filter { it.matchesQuery(query) }
                            SongListScreen(
                                songs = filteredSongs,
                                onSongSelected = { song ->
                                    val mediaItems = filteredSongs.map { it.toMediaItem() }
                                    val startIndex = filteredSongs.indexOf(song).coerceAtLeast(0)
                                    controller.setMediaItems(mediaItems, startIndex, 0L)
                                    controller.prepare()
                                    controller.play()
                                },
                                controller = controller
                            )
                        }
                        2 -> ArtistListScreen(
                            songs = songs,
                            // --- MODIFIED: This now opens the bottom sheet ---
                            onArtistSelected = {
                                // When an artist is selected, just open the sheet.
                                // The navigation will be handled from the sheet itself.
                                isArtistFilterSheetOpen = true
                            }
                            // --- END OF MODIFICATION ---
                        )
                        3 -> PlaylistScreen(
                            playlists = listOf("Favorites", "Cloud"),
                            onPlaylistSelected = { playlist ->
                                navController.navigate("playlist/$playlist")
                            }
                        )
                    }
                }
            }
        }
    }

    // --- NEW: MODAL BOTTOM SHEET RENDER ---
    // This will appear when `isArtistFilterSheetOpen` is true
    if (isArtistFilterSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isArtistFilterSheetOpen = false },
            sheetState = sheetState
        ) {
            val allArtists = songs.mapNotNull { it.artist.takeIf { it.isNotBlank() } }.distinct().sorted()

            ArtistFilterList(
                artists = allArtists,
                selectedArtist = null, // Nothing is pre-selected
                onArtistSelected = { selectedArtist ->
                    // This is where the navigation happens
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            isArtistFilterSheetOpen = false // Reset state
                            // If an artist is selected (not "All Artists"), navigate
                            if (selectedArtist != null) {
                                navController.navigate("artist/$selectedArtist")
                            }
                        }
                    }
                }
            )
        }
    }
    // --- END OF NEW CODE ---
}

private fun Song.matchesQuery(query: String): Boolean {
    return query.isBlank() ||
            title.contains(query, ignoreCase = true) ||
            artist.contains(query, ignoreCase = true)
}
