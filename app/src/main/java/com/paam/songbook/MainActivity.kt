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
import com.paam.songbook.ui.PlayerHost

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

            setContent {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    // 🔹 Wrap everything in PlayerHost
                    PlayerHost(
                        controller = controller!!,
                        songs = songs,
                        lyrics = lyrics
                    ) {
                        NavHost(navController, startDestination = "main") {
                            composable("main") {
                                MainScreen(
                                    songs = songs,
                                    navController = navController,
                                    controller = controller!!
                                )
                            }

                            composable("songDetail/{songId}") { backStackEntry ->
                                val songId = backStackEntry.arguments?.getInt("songId")
                                val song = songs.find { it.songID == songId }
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
