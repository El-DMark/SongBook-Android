package com.paam.songbook.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.ui.components.*
import com.paam.songbook.ui.extractFolder
import kotlinx.coroutines.launch
import androidx.media3.session.MediaController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    navController: NavController,
    controller: MediaController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Artists", "Folders")

    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Options",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("settings")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("about")
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Songs Of Bride") },
                    actions = {
                        IconButton(onClick = { isSearching = !isSearching }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Apps, contentDescription = "Options")
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
                // Animated search bar
                AnimatedVisibility(visible = isSearching) {
                    SearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        onSearch = { /* trigger search */ },
                        active = false,
                        onActiveChange = {},
                        placeholder = { Text("Search songs...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {}
                }

                // Featured row (extracted)
                FeaturedRow(songs = songs, onSongSelected = onSongSelected)

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                // Filtered songs
                val filteredSongs = if (query.isNotBlank()) {
                    songs.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.artist.contains(query, ignoreCase = true)
                    }
                } else songs

                SongList(
                    songs = filteredSongs,
                    onSongSelected = onSongSelected,
                    isLoading = songs.isEmpty()
                )
            }
        }
    }
}
