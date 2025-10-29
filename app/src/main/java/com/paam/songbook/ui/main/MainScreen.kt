package com.paam.songbook.ui.main

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.model.toMediaItem
import com.paam.songbook.ui.artists.ArtistListScreen
import com.paam.songbook.ui.home.HomeScreen
import com.paam.songbook.ui.playlists.PlaylistScreen
import com.paam.songbook.ui.songs.SongListScreen
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun MainScreen(
    songs: List<Song>,
    navController: NavController,
    controller: MediaController
) {
    val tabs = listOf("Home", "Songs", "Artists", "Playlists")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        AnimatedContent(
                            targetState = isSearching,
                            transitionSpec = {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                        slideOutHorizontally { -it } + fadeOut()
                            },
                            label = "SearchTransition"
                        ) { searching ->
                            if (searching) {
                                val focusRequester = remember { FocusRequester() }
                                LaunchedEffect(Unit) { focusRequester.requestFocus() }

                                TextField(
                                    value = query,
                                    onValueChange = { query = it },
                                    placeholder = { Text("Search songs...") },

                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester),
                                    shape = RoundedCornerShape(35.dp),
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                    ),
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            if (query.isNotEmpty()) query = "" else isSearching = false
                                        }) {
                                            Icon(Icons.Default.Close, contentDescription = "Close")
                                        }
                                    }
                                )
                            } else {
                                Text("Songs of Bride")
                            }
                        }
                    },
                    actions = {
                        if (!isSearching) {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                        IconButton(onClick = { navController.navigate("about") }) {
                            Icon(Icons.Default.Info, contentDescription = "About")
                        }
                    }
                )
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState)
            ) { page ->
                when (page) {
                    0 -> HomeScreen(
                        songs = songs,
//                       // onSongSelected = { song ->
//                            val mediaItems = songs.map { it.toMediaItem() }
//                            val startIndex = songs.indexOf(song).coerceAtLeast(0)
//                            controller.setMediaItems(mediaItems, startIndex, 0L)
//                            controller.prepare()
//                            controller.play()
//                        },
                        navController = navController,
                        controller = controller,
                        query = query
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
                            }
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

// 🔹 Helper extension for filtering
private fun Song.matchesQuery(query: String): Boolean {
    return query.isBlank() ||
            title.contains(query, ignoreCase = true) ||
            artist.contains(query, ignoreCase = true)
}
