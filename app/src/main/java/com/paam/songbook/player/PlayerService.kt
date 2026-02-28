package com.paam.songbook.player
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.paam.songbook.data.DownloadUtil // 🔹 We will create this next

@UnstableApi
class PlayerService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        // 1. Create a DataSource factory that understands Caching
        // This tells ExoPlayer: "Look in the phone storage first, then the web."
        val cacheDataSourceFactory = DownloadUtil.getCacheDataSourceFactory(this)

        // 2. Build the MediaSourceFactory using our cache
        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(cacheDataSourceFactory)

        // 3. Build the Player with the custom factory
        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.player?.release() // Ensure player is released properly
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }
}