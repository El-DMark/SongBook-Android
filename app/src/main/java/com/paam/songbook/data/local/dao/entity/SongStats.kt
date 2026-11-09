package com.paam.songbook.data.local.dao.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_stats")
data class SongStats(
    @PrimaryKey
    val songId: Int,
    val playCount: Int = 0
)
