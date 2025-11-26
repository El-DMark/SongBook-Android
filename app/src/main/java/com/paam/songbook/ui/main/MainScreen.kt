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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.Model.Song
import com.paam.songbook.media.toMediaItem
import com.paam.songbook.ui.artists.ArtistListScreen
import com.paam.songbook.ui.home.HomeScreen
import com.paam.songbook.ui.playlists.PlaylistScreen
import com.paam.songbook.ui.songs.SongListScreen

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
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedPlaceholderColor = Color.LightGray,
                                    unfocusedPlaceholderColor = Color.LightGray
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
                            onArtistSelected = { artist ->
                                navController.navigate("artist/$artist")
                            }
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
}

private fun Song.matchesQuery(query: String): Boolean {
    return query.isBlank() ||
            title.contains(query, ignoreCase = true) ||
            artist.contains(query, ignoreCase = true)
}
