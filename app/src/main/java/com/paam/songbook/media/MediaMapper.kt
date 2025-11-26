package com.paam.songbook.media

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.paam.songbook.Model.Song

// ✅ Convert Song → MediaItem with full metadata
fun Song.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(songID.toString()) // use songID as stable ID
        .setUri(url)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setTrackNumber(songID) // ✅ carry numeric ID through
                .setArtworkUri(Uri.parse(albumArt))
                .setExtras(Bundle().apply {
                    putString("lyrics", lyrics)
                    putString("language", Language)
                    putString("timestamp", timestamp)
                })
                .build()
        )
        .build()
}

// ✅ Convert MediaItem → Song, restoring full metadata
fun MediaItem.toSong(): Song {
    val md = mediaMetadata
    val uri = localConfiguration?.uri?.toString() ?: ""
    val extras = md.extras

    return Song(
        songID = md.trackNumber ?: 0,
        title = md.title?.toString().orEmpty().ifBlank { "Unknown Title" },
        artist = md.artist?.toString().orEmpty().ifBlank { "Unknown Artist" },
        url = uri,
        albumArt = md.artworkUri?.toString().orEmpty(),
        lyrics = extras?.getString("lyrics") ?: "No Lyrics",
        Language = extras?.getString("Language") ?: "Unknown",
        timestamp = extras?.getString("timestamp") ?: "1965-02-28"
    )
}
fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}