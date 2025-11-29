package com.paam.songbook.model

import com.google.gson.annotations.SerializedName

// Root wrapper for JSON
data class PlaylistsResponse(
    val playlists: List<Playlist>
)

// Single playlist
data class Playlist(
    val id: Int,
    val name: String,
    val description: String?,
    // 1. CHANGE THIS to match the JSON key exactly.
    // We are now mapping the 'coverArtUrl' key from the JSON.
    @SerializedName("coverArtUrl")
    val coverArtUrl: String?,
    val songs: List<Int>
)

