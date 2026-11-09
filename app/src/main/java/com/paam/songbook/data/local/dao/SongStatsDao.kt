package com.paam.songbook.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paam.songbook.data.local.dao.entity.SongStats
import kotlinx.coroutines.flow.Flow

@Dao
interface SongStatsDao {
    @Query("SELECT * FROM song_stats ORDER BY playCount DESC LIMIT :limit")
    fun getTopListenedSongs(limit: Int): Flow<List<SongStats>>

    @Query("SELECT * FROM song_stats WHERE songId = :songId")
    suspend fun getSongStats(songId: Int): SongStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: SongStats)

    // Helper method to increment play count
    suspend fun incrementPlayCount(songId: Int) {
        val currentStats = getSongStats(songId)
        if (currentStats != null) {
            insertOrUpdate(currentStats.copy(playCount = currentStats.playCount + 1))
        } else {
            insertOrUpdate(SongStats(songId = songId, playCount = 1))
        }
    }
}
