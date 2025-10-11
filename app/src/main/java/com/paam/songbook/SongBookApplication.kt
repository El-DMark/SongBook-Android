package com.paam.songbook

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession

/**
 * Custom Application class for SongBook.
 * This is referenced in AndroidManifest.xml as android:name=".SongBookApplication"
 */
class SongBookApplication : Application() {

    // You can keep global singletons here if you want
    lateinit var player: ExoPlayer
        private set

    lateinit var mediaSession: MediaSession
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()

        // Attach a MediaSession for system integration (lock screen, notifications, etc.)
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onTerminate() {
        super.onTerminate()
        // Clean up resources
        mediaSession.release()
        player.release()
    }
}
