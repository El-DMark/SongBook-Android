package com.paam.songbook.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.ui.components.*
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
    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf<String?>(null) }

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
                AnimatedVisibility(visible = isSearching) {
                    SearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        onSearch = {},
                        active = false,
                        onActiveChange = {},
                        placeholder = { Text("Search songs...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {}
                }

                NewlyAddedCarousel(
                    songs = songs,
                    onSongSelected = onSongSelected
                )

                LanguageFilterRow(
                    languages = listOf("Hindi", "English", "Punjabi", "Tamil"),
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { selectedLanguage = it }
                )

                val filteredSongs = songs.filter {
                    (selectedLanguage == null || it.language == selectedLanguage) &&
                            (query.isBlank() || it.title.contains(query, true) || it.artist.contains(query, true))
                }

                SongList(
                    songs = filteredSongs,
                    onSongSelected = onSongSelected,
                    isLoading = songs.isEmpty(),
                    useGrid = true
                )
            }
        }
    }
}
