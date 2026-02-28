package com.paam.songbook.data.local.dao

import androidx.room.*
import com.paam.songbook.data.local.dao.entity.FavoriteSong
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the favorite_songs table.
 */
@Dao
interface FavoriteDao {

    /**
     * Adds a song to favorites.
     * OnConflictStrategy.REPLACE ensures that if the song is already favorited,
     * we just update the timestamp.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favoriteSong: FavoriteSong)

    /**
     * Removes a song from favorites.
     */
    @Delete
    suspend fun removeFavorite(favoriteSong: FavoriteSong)

    /**
     * Retrieves all favorited song IDs as a Flow.
     * This allows the UI (like the Heart icon toggle) to update automatically.
     */
    @Query("SELECT songId FROM favorite_songs ORDER BY addedAt DESC")
    fun getAllFavoriteIds(): Flow<List<String>>

    /**
     * 🔹 NEW: Retrieves all FavoriteSong objects.
     * This is needed for the FavoriteSection on the HomeScreen to
     * map IDs back to full Song objects.
     */
    @Query("SELECT * FROM favorite_songs ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteSong>>

    /**
     * Checks if a specific song is currently favorited.
     * Useful for toggling the Heart icon in the FullPlayer.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE songId = :id)")
    fun isFavorite(id: String): Flow<Boolean>
}