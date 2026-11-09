package com.paam.songbook

import android.content.ComponentName
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import Lyrics
import com.paam.songbook.model.LyricsRepository
import com.paam.songbook.model.Playlist
import com.paam.songbook.model.PlaylistRepository
import com.paam.songbook.model.Song
import com.paam.songbook.model.SongRepository
import com.paam.songbook.media.toMediaItem
import com.paam.songbook.player.PlayerService
import com.paam.songbook.ui.PlayerHost
import com.paam.songbook.ui.about.AboutScreen
import com.paam.songbook.ui.artists.ArtistDetailScreen
import com.paam.songbook.ui.main.MainScreen
import com.paam.songbook.ui.playlists.PlaylistDetailScreen
import com.paam.songbook.ui.songdetail.SongDetailScreen
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull

@UnstableApi
class MainActivity : ComponentActivity() {

    private var controller: MediaController? = null

    // 🔹 States to track loading and data
    private var isReady by mutableStateOf(false)
    private var loadedSongs by mutableStateOf<List<Song>>(emptyList())
    private var loadedLyrics by mutableStateOf<List<Lyrics>>(emptyList())
    private var loadedPlaylists by mutableStateOf<List<Playlist>>(emptyList())
    private var loadedFeaturedSongId by mutableStateOf<Int?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        // 🔹 Install Splash Screen BEFORE super.onCreate
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // 🔹 Drop native splash immediately since we use our custom animated one
        splashScreen.setKeepOnScreenCondition { false }

        // 1. Initialize Room Database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "songbook-database"
        ).fallbackToDestructiveMigration().build()

        val favoriteDao = db.favoriteDao()
        val songStatsDao = db.songStatsDao()

        lifecycleScope.launch {
            val sessionToken = SessionToken(this@MainActivity, ComponentName(this@MainActivity, PlayerService::class.java))
            val controllerFuture = MediaController.Builder(this@MainActivity, sessionToken).buildAsync()
            controller = controllerFuture.await()

            val jsonUrl = "https://el-dmark.github.io/songbook/stream_dev.json"
            val fetchedSongs = SongRepository.loadSongs(this@MainActivity, jsonUrl)
            val lyricsUrl = "https://el-dmark.github.io/songbook/lyricsStream_dev.json"
            val fetchedLyrics = LyricsRepository.loadLyrics(this@MainActivity, lyricsUrl)
            val playlistUrl = "https://el-dmark.github.io/songbook/playlists.json"
            val cloudPlaylistsResponse = PlaylistRepository.loadPlaylists(this@MainActivity, playlistUrl)
            
            loadedSongs = fetchedSongs
            loadedLyrics = fetchedLyrics
            loadedFeaturedSongId = cloudPlaylistsResponse?.featuredSongId

            // Pre-calculate playlists
            val favoriteIdsSet = favoriteDao.getAllFavoriteIds().firstOrNull() ?: emptyList()
            val favoriteSongsList = fetchedSongs.filter { it.songID.toString() in favoriteIdsSet }
            val favoritesPlaylist = Playlist(
                id = 99999,
                name = "My Inspiration",
                songs = favoriteSongsList.map { it.songID },
                coverArtUrl = "https://raw.githubusercontent.com/El-DMark/songbook/d4c703f3c11451a838604267837bcf3cf7bdda67/favsCoverart/favsCoverart.png",
                description = "Source of My Revival"
            )
            loadedPlaylists = listOf(favoritesPlaylist) + (cloudPlaylistsResponse?.playlists ?: emptyList())

            // Ensure the splash animation plays for at least a beautiful 3 seconds
            kotlinx.coroutines.delay(3500)
            
            // Check for deep link to a specific song
            val intentData = intent?.data
            if (intentData != null && intentData.host == "tehillah.live" && intentData.path?.startsWith("/song/") == true) {
                val songIdStr = intentData.path?.removePrefix("/song/")
                val songToPlay = fetchedSongs.find { it.songID.toString() == songIdStr }
                if (songToPlay != null) {
                    controller?.setMediaItem(songToPlay.toMediaItem())
                    controller?.prepare()
                    controller?.play()
                }
            }

            isReady = true
        }

        setContent {
            com.paam.songbook.ui.theme.SongBookTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = androidx.compose.ui.Modifier.fillMaxSize()
                ) {
                    if (!isReady || controller == null) {
                        com.paam.songbook.ui.splash.AnimatedSplashScreen()
                    } else {
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
                                
                                // Increment play count
                                item?.mediaId?.toIntOrNull()?.let { songId ->
                                    lifecycleScope.launch {
                                        songStatsDao.incrementPlayCount(songId)
                                    }
                                }
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

                    // 🔹 Local "Favorites" Playlist is now calculated in launch block so we don't recalculate heavily here
                    val allPlaylists = loadedPlaylists

                    // Reactive check for the heart icon in PlayerHost
                    val isCurrentFavorite = favoriteIds.contains(currentMediaId)

                    // 🔹 Observe Top Listened Songs
                    val topListenedStats by songStatsDao.getTopListenedSongs(7).collectAsState(initial = emptyList())
                    val topListenedSongs = remember(topListenedStats, loadedSongs) {
                        topListenedStats.mapNotNull { stat ->
                            loadedSongs.find { it.songID == stat.songId }
                        }
                    }

                    PlayerHost(
                        controller = controller!!,
                        songs = loadedSongs,
                        lyrics = loadedLyrics,
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
                                    songs = loadedSongs,
                                    playlists = allPlaylists,
                                    navController = navController,
                                    controller = controller!!,
                                    featuredsongid = loadedFeaturedSongId,
                                    favs = allFavEntities,
                                    topListenedSongs = topListenedSongs, // Pass top listened
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
                                    allSongs = loadedSongs,
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
                                    val songsInPlaylist = loadedSongs.filter { selectedPlaylist.songs.contains(it.songID) }
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
                                val song = loadedSongs.find { it.songID.toString() == songId }
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
    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.release()
    }
}
