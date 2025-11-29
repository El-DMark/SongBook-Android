package com.paam.songbook

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.paam.songbook.player.PlayerService
import com.paam.songbook.model.SongRepository
import com.paam.songbook.model.LyricsRepository
import com.paam.songbook.ui.main.MainScreen
import com.paam.songbook.ui.songdetail.SongDetailScreen
import com.paam.songbook.ui.settings.SettingsScreen
import com.paam.songbook.ui.about.AboutScreen
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.paam.songbook.model.PlaylistRepository
import com.paam.songbook.ui.PlayerHost
import com.paam.songbook.ui.artists.ArtistDetailScreen
import com.paam.songbook.ui.playlists.PlaylistDetailScreen

@UnstableApi
class MainActivity : ComponentActivity() {

    private var controller: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val sessionToken = SessionToken(
                this@MainActivity,
                ComponentName(this@MainActivity, PlayerService::class.java)
            )

            val controllerFuture =
                MediaController.Builder(this@MainActivity, sessionToken).buildAsync()
            controller = controllerFuture.await()

            val jsonUrl = "https://el-dmark.github.io/songbook/stream_dev.json"
            val songs = SongRepository.loadSongs(this@MainActivity, jsonUrl)

            val lyricsUrl = "https://el-dmark.github.io/songbook/lyricsStream_dev.json"
            val lyrics = LyricsRepository.loadLyrics(this@MainActivity, lyricsUrl)

            val playlistUrl = "https://el-dmark.github.io/songbook/playlists.json"
            val playlists = PlaylistRepository.loadPlaylists(this@MainActivity, playlistUrl)

            setContent {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    PlayerHost(
                        controller = controller!!,
                        songs = songs,
                        lyrics = lyrics,
                        playlists = playlists
                    ) {
                        NavHost(navController, startDestination = "main") {
                            composable("main") {
                                MainScreen(
                                    songs = songs,
                                    playlists = playlists,
                                    navController = navController,
                                    controller = controller!!
                                )
                            }

                            composable(
                                route = "artist/{artistName}",
                                arguments = listOf(navArgument("artistName") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val artistName =
                                    backStackEntry.arguments?.getString("artistName") ?: ""
                                ArtistDetailScreen(
                                    artistName = artistName,
                                    allSongs = songs,
                                    navController = navController,
                                    mediaController = controller!!
                                )
                            }

                            // --- MODIFICATION START ---
                            composable(
                                route = "playlist/{id}",
                                // 1. Change the argument type to IntType
                                arguments = listOf(navArgument("id") { type = NavType.IntType })
                            ) { backStackEntry ->
                                // 2. Get the argument as an Int. Provide a default value like -1.
                                val playlistId = backStackEntry.arguments?.getInt("id") ?: -1

                                // 3. The comparison is now Int == Int, which works correctly.
                                val selectedPlaylist = playlists.find { it.id == playlistId }
                                val songsInPlaylist = songs.filter { selectedPlaylist?.songs?.contains(it.songID) == true }

                                if (selectedPlaylist != null) {
                                    PlaylistDetailScreen(
                                        playlist = selectedPlaylist,
                                        songs = songsInPlaylist,
                                        navController = navController,
                                        mediaController = controller!!
                                    )
                                }
                            }
                            // --- MODIFICATION END ---

                            composable("songDetail/{songId}") { backStackEntry ->
                                val songId = backStackEntry.arguments?.getString("songId")
                                val song = songs.find { it.songID.toString() == songId }
                                if (song != null) {
                                    SongDetailScreen(song = song)
                                }
                            }

                            composable("settings") {
                                SettingsScreen(onBack = { navController.popBackStack() })
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
