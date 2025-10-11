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
import com.paam.songbook.ui.PlayerScaffold
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

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
            val controllerFuture = MediaController.Builder(this@MainActivity, sessionToken).buildAsync()
            controller = controllerFuture.await()

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
    }
}
