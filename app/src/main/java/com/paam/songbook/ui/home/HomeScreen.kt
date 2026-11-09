package com.paam.songbook.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.paam.songbook.model.Song
import com.paam.songbook.ui.songs.FavoriteSection
import com.paam.songbook.ui.songs.DailyVerseCard
import com.paam.songbook.ui.songs.MostListenedSection
import com.paam.songbook.ui.songs.RecentlyAddedSection
import com.paam.songbook.data.local.dao.entity.FavoriteSong
@Composable
fun HomeScreen(
    songs: List<Song>,
    favs :List<FavoriteSong>,
    navController: NavController,
    controller: MediaController,
    modifier: Modifier = Modifier,
    featuredsongid: Int?,
    topListenedSongs: List<Song> = emptyList()
) {
    val scrollState = rememberScrollState()

    // --- THIS IS THE FIX ---
    // 1. Check if featuredsongid is not null.
    // 2. If it's not null, use it to safely get the song from the list.
    //    We use 'getOrNull' which is safer than 'get()' as it prevents crashes if the id is out of bounds.
    val featuredSong = if (featuredsongid != null) {
        songs.getOrNull(featuredsongid - 1) // Use getOrNull and adjust for zero-based index
    } else {
        songs.getOrNull(1)
    }
    val favoriteSongsList = remember(favs, songs) {
        val favoriteIds = favs.map { it.songId }
        songs.filter { it.songID.toString() in favoriteIds }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 🔹 Daily Verse Card
        DailyVerseCard(modifier = Modifier.fillMaxWidth().height(220.dp))

        if (!favoriteSongsList.isEmpty()) {
            FavoriteSection(
                songs = favoriteSongsList,
                controller = controller
            )
        }
        // 🔹 Recently Added
        RecentlyAddedSection(
            songs = songs,
            controller = controller
        )

        // 🔹 Most Listened
        if (topListenedSongs.isNotEmpty()) {
            MostListenedSection(
                songs = topListenedSongs,
                controller = controller
            )
        } else {
            // Fallback for brand new users with no play history yet: Show random songs
            MostListenedSection(
                songs = songs.shuffled(),
                controller = controller
            )
        }
    }
}
