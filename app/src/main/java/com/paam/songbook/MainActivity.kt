package com.paam.songbook

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.room.Room
import com.paam.songbook.data.local.AppDatabase
import com.paam.songbook.data.local.dao.FavoriteDao
import com.paam.songbook.data.local.dao.entity.FavoriteSong
import com.paam.songbook.model.LyricsRepository
import com.paam.songbook.model.Playlist
import com.paam.songbook.model.PlaylistRepository
import com.paam.songbook.model.Song
import com.paam.songbook.model.SongRepository
import com.paam.songbook.player.PlayerService
import com.paam.songbook.ui.PlayerHost
import com.paam.songbook.ui.about.AboutScreen
import com.paam.songbook.ui.artists.ArtistDetailScreen
import com.paam.songbook.ui.main.MainScreen
import com.paam.songbook.ui.playlists.PlaylistDetailScreen
import com.paam.songbook.ui.songdetail.SongDetailScreen
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

@UnstableApi
class MainActivity : ComponentActivity() {

    private var controller: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Room Database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "songbook-database"
        ).build()

        val favoriteDao = db.favoriteDao()

        lifecycleScope.launch {
            val sessionToken = SessionToken(this@MainActivity, ComponentName(this@MainActivity, PlayerService::class.java))
            val controllerFuture = MediaController.Builder(this@MainActivity, sessionToken).buildAsync()
            controller = controllerFuture.await()

            val jsonUrl = "https://el-dmark.github.io/songbook/stream_dev.json"
            val songs = SongRepository.loadSongs(this@MainActivity, jsonUrl)
            val lyricsUrl = "https://el-dmark.github.io/songbook/lyricsStream_dev.json"
            val lyrics = LyricsRepository.loadLyrics(this@MainActivity, lyricsUrl)
            val playlistUrl = "https://el-dmark.github.io/songbook/playlists.json"
            val cloudPlaylistsResponse = PlaylistRepository.loadPlaylists(this@MainActivity, playlistUrl)
            val featuredSongId = cloudPlaylistsResponse?.featuredSongId

            setContent {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    // 🔹 Observe Local Favorite IDs (String list for Room)
                    val favoriteIds: List<String> by favoriteDao.getAllFavoriteIds().collectAsState(initial = emptyList())

                    // 🔹 Observe full Favorite Entities (for lists)
                    val allFavEntities: List<FavoriteSong> by favoriteDao.getAllFavorites().collectAsState(initial = emptyList())

                    // 🔹 Convert IDs for screens that require Set<Long>
                    val favoriteLongIds = remember(favoriteIds) {
                        favoriteIds.mapNotNull { it.toLongOrNull() }.toSet()
                    }

                    // 🔹 Track current media ID manually for reactivity
                    var currentMediaId by remember { mutableStateOf(controller?.currentMediaItem?.mediaId) }

                    DisposableEffect(controller) {
                        val listener = object : Player.Listener {
                            override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
                                currentMediaId = item?.mediaId
                            }
                        }
                        controller?.addListener(listener)
                        onDispose { controller?.removeListener(listener) }
                    }

                    // 🔹 Logic to toggle favorites from any screen
                    val toggleFavorite: (Song) -> Unit = { song ->
                        val targetId = song.songID.toString()
                        val isFav = favoriteIds.contains(targetId)
                        lifecycleScope.launch {
                            if (isFav) {
                                favoriteDao.removeFavorite(FavoriteSong(targetId))
                            } else {
                                favoriteDao.addFavorite(FavoriteSong(targetId))
                            }
                        }
                    }

                    // 4. Create the Local "Favorites" Playlist Object
                    val favoriteSongsList = songs.filter { it.songID.toString() in favoriteIds }
                    val favoritesPlaylist = Playlist(
                        id = 99999,
                        name = "My Inspiration",
                        songs = favoriteSongsList.map { it.songID },
                        coverArtUrl = "https://raw.githubusercontent.com/El-DMark/songbook/d4c703f3c11451a838604267837bcf3cf7bdda67/favsCoverart/favsCoverart.png",
                        description = "Source of My Revival"
                    )

                    val allPlaylists = listOf(favoritesPlaylist) + (cloudPlaylistsResponse?.playlists ?: emptyList())

                    // Reactive check for the heart icon in PlayerHost
                    val isCurrentFavorite = favoriteIds.contains(currentMediaId)

                    PlayerHost(
                        controller = controller!!,
                        songs = songs,
                        lyrics = lyrics,
                        playlists = allPlaylists,
                        isFavorite = isCurrentFavorite,
                        onToggleFavorite = { alreadyFav ->
                            val targetId = currentMediaId
                            if (targetId != null) {
                                lifecycleScope.launch {
                                    if (alreadyFav) {
                                        favoriteDao.removeFavorite(FavoriteSong(targetId))
                                    } else {
                                        favoriteDao.addFavorite(FavoriteSong(targetId))
                                    }
                                }
                            }
                        }
                    ) {
                        NavHost(navController, startDestination = "main") {
                            composable("main") {
                                MainScreen(
                                    songs = songs,
                                    playlists = allPlaylists,
                                    navController = navController,
                                    controller = controller!!,
                                    featuredsongid = featuredSongId,
                                    favs = allFavEntities,
                                    onToggleFavorite = toggleFavorite
                                )
                            }

                            composable(
                                route = "artist/{artistName}",
                                arguments = listOf(navArgument("artistName") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val artistName = backStackEntry.arguments?.getString("artistName") ?: ""
                                ArtistDetailScreen(
                                    artistName = artistName,
                                    allSongs = songs,
                                    favoriteIds = favoriteLongIds,
                                    navController = navController,
                                    mediaController = controller!!,
                                    onToggleFavorite = toggleFavorite
                                )
                            }

                            composable(
                                route = "playlist/{id}",
                                arguments = listOf(navArgument("id") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val playlistId = backStackEntry.arguments?.getInt("id") ?: -1
                                val selectedPlaylist = allPlaylists.find { it.id == playlistId }
                                if (selectedPlaylist != null) {
                                    val songsInPlaylist = songs.filter { selectedPlaylist.songs.contains(it.songID) }
                                    PlaylistDetailScreen(
                                        playlist = selectedPlaylist,
                                        songs = songsInPlaylist,
                                        favs = allFavEntities,
                                        navController = navController,
                                        mediaController = controller!!,
                                        onToggleFavorite = toggleFavorite
                                    )
                                }
                            }

                            composable("songDetail/{songId}") { backStackEntry ->
                                val songId = backStackEntry.arguments?.getString("songId")
                                val song = songs.find { it.songID.toString() == songId }
                                if (song != null) { SongDetailScreen(song = song) }
                            }

                            composable("about") {
                                AboutScreen(onBack = { navController.popBackStack() })
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.release()
    }
}