package com.paam.songbook.media

import androidx.media3.common.MediaItem
import com.paam.songbook.model.Song

fun MediaItem.toSong(): Song {
    val md = mediaMetadata
    val uri = localConfiguration?.uri?.toString() ?: ""

    return Song(
        songID = uri.hashCode(), // stable ID based on URI
        title = md.title?.toString().orEmpty().ifBlank { "Unknown Title" },
        artist = md.artist?.toString().orEmpty().ifBlank { "Unknown Artist" },
        url = uri,
        albumArt = md.artworkUri?.toString().orEmpty(),
        lyrics = "",
        Language = md.extras?.getString("Language") ?: "Unknown",
        // ✅ store as String, since your JSON has "2025-10-22"
        timestamp = md.extras?.getString("timestamp") ?: "1970-01-01"
    )
}
