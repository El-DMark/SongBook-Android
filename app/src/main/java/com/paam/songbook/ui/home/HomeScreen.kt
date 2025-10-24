package com.paam.songbook.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.ui.components.*
import kotlinx.coroutines.launch

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


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AnimatedContent(
                        targetState = isSearching,
                        transitionSpec = {
                            slideInHorizontally { fullWidth -> fullWidth } + fadeIn() togetherWith
                                    slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
                        },
                        label = "SearchTransition"
                    ) { searching ->
                        if (searching) {
                            val focusRequester = remember { FocusRequester() }
                            LaunchedEffect(Unit) { focusRequester.requestFocus() }

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                TextField(
                                    value = query,
                                    onValueChange = { query = it },
                                    placeholder = { Text("Search songs...") },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f) // 90% of app bar width
                                        .focusRequester(focusRequester),
                                    shape = RoundedCornerShape(35.dp), // pill corners
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
                            }
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
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Apps, contentDescription = "Options")
                    }
                }
            )
        },
        modifier = modifier
    ) {  padding ->
        Box(
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(LayoutDirection.Ltr)
                )
                .fillMaxSize()
        ) {
            Column(Modifier.fillMaxSize()) {
                NewlyAddedCarousel(
                    songs = songs,
                    onSongSelected = onSongSelected
                )

                LanguageFilterRow(
                    languages = listOf("Hindi", "English"),
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { selectedLanguage = it }
                )

                val filteredSongs = songs.filter {
                    (selectedLanguage == null || it.Language == selectedLanguage) &&


                            (query.isBlank() || it.title.contains(query, true) || it.artist.contains(query, true))
                }


                SongList(
                    songs = filteredSongs,
                    onSongSelected = onSongSelected,
                    isLoading = songs.isEmpty(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

}
