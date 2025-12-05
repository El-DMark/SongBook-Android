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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily.Companion.Cursive
import androidx.compose.ui.unit.sp
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.media.toMediaItem
import com.paam.songbook.model.Playlist
import com.paam.songbook.model.Song
import com.paam.songbook.ui.artists.ArtistListScreen
import com.paam.songbook.ui.home.HomeScreen
import com.paam.songbook.ui.playlistsimport.PlaylistScreen // Corrected import
import com.paam.songbook.ui.songs.SongListScreen
import androidx.compose.material.icons.filled.Info
import kotlin.Int

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    songs: List<Song>,
    playlists: List<Playlist>,
    navController: NavController,
    controller: MediaController,
    featuredsongid: Int?
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    // When the user switches tabs, exit search mode and clear the query
    LaunchedEffect(pagerState.currentPage) {
        isSearching = false
        query = ""
    }

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
                                placeholder = {
                                    // 4. Dynamic placeholder text
                                    val placeholderText = when (pagerState.currentPage) {
                                        1 -> "Search hymns..."
                                        2 -> "Search saints..."
                                        3 -> "Search playlists..."
                                        else -> "Search..."
                                    }
                                    Text(placeholderText, color = Color.LightGray)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                // 6. FIX API CALL for TextField colors
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.Transparent,
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
                            val title = when (pagerState.currentPage) {
                                0 -> "Tehillah"
                                1 -> "Hymns"
                                2 -> "Saints"
                                3 -> "Playlists"
                                else -> "Tehillah" // Fallback
                            }
                            Text(title, color = Color.White, fontFamily = Cursive, fontSize = 42.sp)
                        }
                    },
                    actions = {
                        // 3. Hide Search on Home Screen (Page 0)
                        if (!isSearching && pagerState.currentPage != 0) {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                            }
                        }
                        IconButton(onClick = { navController.navigate("about") }) {
                            Icon(Icons.Default.Info, contentDescription = "About", tint = Color.White)
                        }
                    },
                    // 6. FIX API CALL for TopAppBar colors
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
                    // 1. Context-aware filtering
                    when (page) {
                        0 -> HomeScreen(
                            songs = songs,
                            navController = navController,
                            controller = controller,
                            featuredsongid = featuredsongid
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
                            // ArtistListScreen gets a filtered list of songs to derive the artists
                            songs = songs.filter { it.matchesQuery(query) },
                            onArtistSelected = { artistName ->
                                navController.navigate("artist/$artistName")
                            }
                        )
                        3 -> PlaylistScreen(
                            // PlaylistScreen gets a filtered list of playlists
                            playlists = playlists.filter { it.matchesQuery(query) },
                            onPlaylistSelected = { playlist ->
                                navController.navigate("playlist/${playlist.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

// Existing filter function for Songs (used for Hymns and Saints)
private fun Song.matchesQuery(query: String): Boolean {
    return query.isBlank() ||
            title.contains(query, ignoreCase = true) ||
            artist.contains(query, ignoreCase = true)
}

// 2. New filter function for Playlists
private fun Playlist.matchesQuery(query: String): Boolean {
    return query.isBlank() ||
            name.contains(query, ignoreCase = true) ||
            description?.contains(query, ignoreCase = true) == true
}
//Mark David