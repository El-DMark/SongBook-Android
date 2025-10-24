package com.paam.songbook.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit,
    navController: NavController,
    controller: MediaController,
    query: String, // 🔹 passed down from MainScreen
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val tabs = listOf("Language")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        // 🔹 Carousel
        NewlyAddedCarousel(
            songs = songs,
            onSongSelected = onSongSelected
        )

        // 🔹 Filter tabs (Language, Artist, etc.)
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } }//,
                  //  text = { Text(title) }
                )
            }
        }

        // 🔹 Pager for filters
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            flingBehavior = PagerDefaults.flingBehavior(state = pagerState)
        ) { page ->
            when (page) {
                0 -> LanguageFilterRow(
                    languages = listOf("Hindi", "English"),
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { selectedLanguage = it }
                )
            }
        }

        // 🔹 Apply filters + search
        val filteredSongs = songs.filter {
            (selectedLanguage == null || it.Language == selectedLanguage) &&
                    (query.isBlank() ||
                            it.title.contains(query, ignoreCase = true) ||
                            it.artist.contains(query, ignoreCase = true))
        }

        // 🔹 Song list
        SongList(
            songs = filteredSongs,
            onSongSelected = onSongSelected,
            isLoading = songs.isEmpty(),
            modifier = Modifier.fillMaxSize()
        )
    }
}
