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
import com.paam.songbook.ui.main.MainScreen
import com.paam.songbook.ui.settings.SettingsScreen
import com.paam.songbook.ui.about.AboutScreen
import com.paam.songbook.ui.songs.SongListScreen
import com.paam.songbook.model.SongRepository
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import androidx.navigation.compose.*
import com.paam.songbook.model.Song
import com.paam.songbook.model.toMediaItem
import com.paam.songbook.ui.MainScaffold

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

            // 🔹 Load songs once here
            val jsonUrl =
                "https://drive.google.com/uc?export=download&id=14l-TYjjaUOL0oiLU5owowbkfMp1vxH_C"
            val songs = SongRepository.loadSongs(this@MainActivity, jsonUrl)

            setContent {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController, startDestination = "main") {
                        composable("main") {
                            // 🔹 Pass songs down
                            MainScaffold(
                                controller = controller!!,
                                navController = navController,
                                songs = songs
                            )
                        }
                        composable("settings") {
                            SettingsScreen(onBack = { navController.popBackStack() })
                        }
                        composable("about") {
                            AboutScreen(onBack = { navController.popBackStack() })
                        }
                        composable("artist/{artistName}") { backStackEntry ->
                            val artistName = backStackEntry.arguments?.getString("artistName") ?: ""
                            val artistSongs = songs.filter { it.artist == artistName }
                            SongListScreen(
                                songs = artistSongs,
                                onSongSelected = { song ->
                                    val mediaItems = artistSongs.map { it.toMediaItem() }
                                    val startIndex = artistSongs.indexOf(song).coerceAtLeast(0)
                                    controller?.apply {
                                        setMediaItems(mediaItems, startIndex, 0L)
                                        prepare()
                                        play()
                                    }
                                }
                            )
                        }
                        composable("playlist/{playlistName}") { backStackEntry ->
                            val playlistName =
                                backStackEntry.arguments?.getString("playlistName") ?: ""
                            val playlistSongs = emptyList<Song>() // placeholder
                            SongListScreen(
                                songs = playlistSongs,
                                onSongSelected = { song ->
                                    val mediaItems = playlistSongs.map { it.toMediaItem() }
                                    val startIndex = playlistSongs.indexOf(song).coerceAtLeast(0)
                                    controller?.apply {
                                        setMediaItems(mediaItems, startIndex, 0L)
                                        prepare()
                                        play()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        fun onDestroy() {
            super.onDestroy()
            controller?.release()
        }
    }
}
