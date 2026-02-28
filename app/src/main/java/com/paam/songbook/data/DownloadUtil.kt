package com.paam.songbook.data

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(UnstableApi::class)
object DownloadUtil {
    private var cache: SimpleCache? = null

    /**
     * 1. Initialize the storage folder.
     * Uses a singleton pattern to ensure only one instance of SimpleCache exists.
     */
    fun getCache(context: Context): SimpleCache {
        if (cache == null) {
            val cacheDir = File(context.cacheDir, "media_cache")
            val databaseProvider = StandaloneDatabaseProvider(context)
            // NoOpCacheEvictor ensures files stay until disk is full (no automatic deletion)
            cache = SimpleCache(cacheDir, NoOpCacheEvictor(), databaseProvider)
        }
        return cache!!
    }

    /**
     * 2. Cache DataSource Factory.
     * This tells the Media3 Player: "Check local cache first; if not found, stream from internet."
     */
    // Inside DownloadUtil.kt

    fun getCacheDataSourceFactory(context: Context): DataSource.Factory {
        val upstreamFactory = DefaultHttpDataSource.Factory()
        return CacheDataSource.Factory()
            .setCache(getCache(context))
            .setUpstreamDataSourceFactory(upstreamFactory)
            // 🔹 CHANGE: Set flags to NOT write to cache during normal playback
            // This prevents the "auto-download" when the song plays.
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
    /**
     * Downloads media to the cache folder with real-time progress reporting.
     * @param onProgress Callback returning 0.0 to 100.0
     */
    suspend fun downloadMedia(context: Context, url: String?, onProgress: (Double) -> Unit = {}) {
        if (url.isNullOrBlank()) return

        withContext(Dispatchers.IO) {
            try {
                val cache = getCache(context)
                val dataSource = DefaultHttpDataSource.Factory().createDataSource()
                val dataSpec = DataSpec(Uri.parse(url))

                val cacheWriter = CacheWriter(
                    CacheDataSource(cache, dataSource),
                    dataSpec,
                    null,
                    object : CacheWriter.ProgressListener {
                        override fun onProgress(requestLength: Long, bytesCached: Long, newBytesCached: Long) {
                            if (requestLength > 0) {
                                val progress = (bytesCached * 100.0) / requestLength
                                onProgress(progress)
                            }
                        }
                    }
                )

                // This blocks the IO thread until the download is finished
                cacheWriter.cache()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 🔹 Checks if a song is already downloaded.
     * Uses getCachedBytes to verify if data exists on disk for this specific URL.
     */
    fun isDownloaded(context: Context, url: String?): Boolean {
        // Safety check: Media3 will crash with IllegalArgumentException if URL is blank
        if (url.isNullOrEmpty() || url.isBlank()) {
            return false
        }

        return try {
            val cache = getCache(context)

            // We check the cached bytes for the entire file range (0 to -1)
            // If bytes are found, we consider it available for offline play.
            val cachedBytes = cache.getCachedBytes(url, 0, -1)

            cachedBytes > 0
        } catch (e: Exception) {
            // Log error if needed, but return false to prevent app crash
            false
        }
    }

    /**
     * Removes downloaded media from the cache.
     */
    fun removeDownload(context: Context, url: String?) {if (url.isNullOrBlank()) return
        try {
            val cache = getCache(context)
            // This removes all cached spans for the given URL
            cache.removeResource(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}