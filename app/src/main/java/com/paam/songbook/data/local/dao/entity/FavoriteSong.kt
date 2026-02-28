package com.paam.songbook.data.local.dao.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a song that has been "favorited" by the user.
 * We only store the songId locally to keep the database lightweight.
 */
@Entity(tableName = "favorite_songs")
data class FavoriteSong(
    @PrimaryKey
    val songId: String,

    /**
     * Optional: Timestamp to allow sorting favorites by "Recently Added"
     */
    val addedAt: Long = System.currentTimeMillis()
)