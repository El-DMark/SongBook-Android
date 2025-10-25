package com.paam.songbook.media

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.paam.songbook.model.Song

// ✅ Convert Song → MediaItem with full metadata
fun Song.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(url)
        .setUri(url)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setArtworkUri(Uri.parse(albumArt))
                .setExtras(Bundle().apply {
                    putString("songID", songID)
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
        songID = extras?.getString("songID") ?: "Unknown",//uri.hashCode(), // stable ID based on URI
        title = md.title?.toString().orEmpty().ifBlank { "Unknown Title" },
        artist = md.artist?.toString().orEmpty().ifBlank { "Unknown Artist" },
        url = uri,
        albumArt = md.artworkUri?.toString().orEmpty(),
        lyrics = extras?.getString("lyrics") ?: "No Lyrics",
        Language = extras?.getString("language") ?: "Unknown",
        timestamp = extras?.getString("timestamp") ?: "1970-01-01"
    )
}
