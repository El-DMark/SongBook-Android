package com.paam.songbook.media

import androidx.media3.common.MediaItem
import com.paam.songbook.model.Song

fun MediaItem.toSong(): Song {
    val md = mediaMetadata
    val uri = localConfiguration?.uri?.toString() ?: ""
    return Song(
        songID = (md.title?.toString() + md.artist?.toString() + uri).hashCode(),
        title = md.title?.toString() ?: "Unknown Title",
        artist = md.artist?.toString() ?: "Unknown Artist",
        url = uri,
        albumArt = md.artworkUri?.toString() ?: "",
        lyrics = "" // Add extras support if needed
    )
}
