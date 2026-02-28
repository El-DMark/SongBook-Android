package com.paam.songbook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
// 🔹 Add these missing imports to link your Dao and Entity
import com.paam.songbook.data.local.dao.FavoriteDao
import com.paam.songbook.data.local.dao.entity.FavoriteSong

@Database(entities = [FavoriteSong::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // This function allows you to access the Dao from the database instance
    abstract fun favoriteDao(): FavoriteDao
}