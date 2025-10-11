package com.paam.songbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import com.paam.songbook.ui.PlayerScaffold
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.await

@UnstableApi
class MainActivity : ComponentActivity() {

    private var mediaSession: MediaSession? = null
    private var controller: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Build ExoPlayer
        val player = ExoPlayer.Builder(this).build()

        // Create MediaSession
        mediaSession = MediaSession.Builder(this, player).build()

        // Build MediaController asynchronously using coroutines
        lifecycleScope.launch {
            val controllerFuture = MediaController.Builder(this@MainActivity, mediaSession!!.token).buildAsync()
            controller = controllerFuture.await() // suspends until ready

            setContent {
                Surface(color = MaterialTheme.colorScheme.background) {
                    controller?.let {
                        PlayerScaffold(controller = it)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller?.release()
        mediaSession?.release()
    }
}
