package com.paam.songbook.ui

import Lyrics
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.gson.Gson
import com.paam.songbook.model.Song
import com.paam.songbook.ui.components.PlayerScaffold
import com.paam.songbook.media.toSong
import com.paam.songbook.model.Playlist
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerHost(
    controller: MediaController,
    songs: List<Song>,
    lyrics: List<Lyrics>,
    playlists: List<Playlist>,
    // 🔹 Added parameters to support Favorites implementation
    isFavorite: Boolean = false,
    onToggleFavorite: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    var currentSong by remember { mutableStateOf<Song?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0L) }
    var position by remember { mutableStateOf(0L) }
    var shuffleModeEnabled by remember { mutableStateOf(controller.shuffleModeEnabled) }
    var repeatMode by remember { mutableStateOf(controller.repeatMode) }
    
    // 🔹 Advanced Sleep Timer State
    var sleepTimerEnd by remember { mutableStateOf<Long?>(null) }
    var stopAfterCurrentTrack by remember { mutableStateOf(false) }

    // 🔹 Handle Sleep Timer Countdown
    LaunchedEffect(sleepTimerEnd) {
        if (sleepTimerEnd != null) {
            while (true) {
                val remaining = sleepTimerEnd!! - System.currentTimeMillis()
                if (remaining <= 0) {
                    controller.pause()
                    sleepTimerEnd = null
                    break
                }
                delay(1000)
            }
        }
    }

    // Listen for song changes and playback state
    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
                if (stopAfterCurrentTrack && reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    controller.pause()
                    stopAfterCurrentTrack = false
                }
                currentSong = item?.toSong()
                duration = controller.duration.coerceAtLeast(0L)
            }

            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }
            override fun onShuffleModeEnabledChanged(shuffleModeEnabledChanged: Boolean) {
                shuffleModeEnabled = shuffleModeEnabledChanged
            }

            override fun onRepeatModeChanged(repeatModeChanged: Int) {
                repeatMode = repeatModeChanged
            }
        }
        controller.addListener(listener)

        // 🔹 Initialize immediately so first song shows up
        currentSong = controller.currentMediaItem?.toSong()
        isPlaying = controller.isPlaying
        duration = controller.duration.coerceAtLeast(0L)
        shuffleModeEnabled = controller.shuffleModeEnabled
        repeatMode = controller.repeatMode

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
        // 🔹 Pass the favorite states down to the Scaffold
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onPlayPause = {
            if (controller.isPlaying) controller.pause() else controller.play()
        },
        onNext = { controller.seekToNext() },
        onPrevious = { controller.seekToPrevious() },
        onSeek = { seekPos -> controller.seekTo(seekPos) },
        shuffleModeEnabled = shuffleModeEnabled,
        repeatMode = repeatMode,
        onToggleShuffle = { controller.shuffleModeEnabled = !controller.shuffleModeEnabled },
        onToggleRepeat = { 
            controller.repeatMode = when (controller.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
        },
        sleepTimerEnd = sleepTimerEnd,
        stopAfterCurrentTrack = stopAfterCurrentTrack,
        onSetSleepTimer = { minutes ->
            if (minutes == 0) {
                sleepTimerEnd = null
            } else {
                sleepTimerEnd = System.currentTimeMillis() + (minutes * 60 * 1000L)
            }
        },
        onSetStopAfterCurrentTrack = { stopAfterCurrentTrack = it },
        lyrics = lyrics
    ) {
        Box(modifier = Modifier.padding(bottom = 0.dp)) {
            content()
        }
    }
}