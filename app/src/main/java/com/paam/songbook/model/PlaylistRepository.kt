package com.paam.songbook.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import com.paam.songbook.model.Playlist
// --- THIS IS THE FIX ---
// Add the missing import for the wrapper class.
import com.paam.songbook.model.PlaylistsResponse

/**
 * Manages loading playlist data from a remote URL with local caching.
 * Follows the same logic as SongRepository.
 */
object PlaylistRepository {
    // Use a different preference name to keep caches separate
    private const val PREF_NAME = "playlist_cache"
    private const val KEY_JSON = "cached_playlist_json"

    /**
     * Loads a list of playlists.
     *
     * It first tries to fetch from the network. On success, it caches the result.
     * On failure, it attempts to load from the cache.
     * If both fail, it returns an empty list.
     *
     * @param context The application context.
     * @param jsonUrl The URL of the JSON file containing playlist data.
     * @param useCacheOnly If true, skips the network request and loads only from cache.
     * @return A list of Playlist objects.
     */
    suspend fun loadPlaylists(context: Context, jsonUrl: String, useCacheOnly: Boolean = false): List<Playlist> {
        return try {
            val json = if (useCacheOnly) {
                getCachedJson(context) ?: throw Exception("No cached playlist data available")
            } else {
                val fetched = fetchJson(jsonUrl)
                cacheJson(context, fetched)
                fetched
            }
            parseJson(json)
        } catch (e: Exception) {
            println("⚠️ Failed to load playlists from network: ${e.message}")
            // Fallback to cache
            getCachedJson(context)?.let {
                println("📦 Using cached playlist data")
                parseJson(it)
            } ?: run {
                println("❌ No cached playlist data available")
                emptyList()
            }
        }
    }

    /**
     * Fetches JSON content from a given URL.
     */
    private suspend fun fetchJson(url: String): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("HTTP error fetching playlists: ${response.code}")
        }

        response.body?.string() ?: throw Exception("Empty response body for playlists")
    }

    /**
     * Caches the provided JSON string in SharedPreferences.
     */
    private fun cacheJson(context: Context, json: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_JSON, json)
            .apply()
    }

    /**
     * Clears the playlist cache from SharedPreferences.
     */
    fun clearCache(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_JSON)
            .apply()
    }

    /**
     * Retrieves the cached JSON string from SharedPreferences.
     */
    private fun getCachedJson(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_JSON, null)
    }

    /**
     * Parses a JSON string into a list of Playlist objects.
     */
    private fun parseJson(json: String): List<Playlist> {
        return try {
            // This line will now compile correctly because of the new import.
            val type = object : TypeToken<PlaylistsResponse>() {}.type

            val response = Gson().fromJson<PlaylistsResponse>(json, type)

            response.playlists ?: emptyList()

        } catch (e: Exception) {
            println("❌ Playlist JSON parsing failed: ${e.message}")
            emptyList()
        }
    }
}
