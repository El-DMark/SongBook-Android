package com.paam.songbook.ui

import Lyrics
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.gson.Gson
import com.paam.songbook.Model.Song
import com.paam.songbook.ui.components.PlayerScaffold
import com.paam.songbook.media.toSong
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerHost(
    controller: MediaController,
    songs: List<Song>,
    lyrics: List<Lyrics>,
    content: @Composable () -> Unit
) {
    var currentSong by remember { mutableStateOf<Song?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0L) }
    var position by remember { mutableStateOf(0L) }

    // Listen for song changes and playback state
    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
                currentSong = item?.toSong()
                duration = controller.duration.coerceAtLeast(0L)

                currentSong?.let {
                    val json = Gson().toJson(it)
                    Log.d("CurrentSongJSON", json)
                    Log.d("LyricsDebug", "Lyrics: ${it.lyrics}")
                }
            }

            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }
        }
        controller.addListener(listener)

        // 🔹 Initialize immediately so first song shows up
        currentSong = controller.currentMediaItem?.toSong()
        isPlaying = controller.isPlaying
        duration = controller.duration.coerceAtLeast(0L)

        currentSong?.let {
            val json = Gson().toJson(it)
            Log.d("CurrentSongJSON", json)
            Log.d("LyricsDebug", "Lyrics: ${it.lyrics}")
        }

        onDispose { controller.removeListener(listener) }
    }

    // Poll playback position while playing
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            position = controller.currentPosition
            duration = controller.duration.coerceAtLeast(0L)
            delay(1000) // update every second
        }
    }

    // ✅ Always mount PlayerScaffold
    PlayerScaffold(
        currentSong = currentSong,
        isPlaying = isPlaying,
        position = position,
        duration = duration,
        onPlayPause = {
            if (controller.isPlaying) controller.pause() else controller.play()
        },
        onNext = { controller.seekToNext() },
        onPrevious = { controller.seekToPrevious() },
        onSeek = { seekPos -> controller.seekTo(seekPos) },
        lyrics = lyrics
    ) {
        Box(modifier = Modifier.padding(bottom = 0.dp)) {
            content()
        }
    }
}
