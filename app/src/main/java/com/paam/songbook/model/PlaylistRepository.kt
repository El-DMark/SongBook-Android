package com.paam.songbook.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Manages loading playlist data from a remote URL with local caching.
 * Loads the entire response, including the list of playlists and the featured song ID.
 */
object PlaylistRepository {

    private const val PREF_NAME = "playlist_cache"
    private const val KEY_JSON = "cached_playlist_json"

    /**
     * Loads the entire playlist response structure (playlists and featured song).
     *
     * It first tries to fetch from the network. On success, it caches the result.
     * On failure, it attempts to load from the last cached version.
     * If both network and cache fail, it returns null.
     *
     * @param context The application context for accessing SharedPreferences.
     * @param jsonUrl The URL of the remote JSON file.
     * @param useCacheOnly If true, forces the repository to only use the local cache.
     * @return A PlaylistsResponse object or null on complete failure.
     */
    suspend fun loadPlaylists(
        context: Context,
        jsonUrl: String,
        useCacheOnly: Boolean = false
    ): PlaylistsResponse? {
        return try {
            val json = if (useCacheOnly) {
                getCachedJson(context) ?: throw Exception("No cached playlist data available")
            } else {
                val fetchedJson = fetchJson(jsonUrl)
                cacheJson(context, fetchedJson)
                fetchedJson
            }
            parseJson(json)
        } catch (e: Exception) {
            println("⚠️ Failed to load playlists from network: ${e.message}")
            // Fallback to cache if network fails
            getCachedJson(context)?.let { cachedJson ->
                println("📦 Using cached playlist data as fallback.")
                parseJson(cachedJson)
            } ?: run {
                println("❌ Network and cache both failed. No playlist data available.")
                null
            }
        }
    }

    /**
     * Fetches the JSON content as a string from a given URL using OkHttp.
     * This function runs on the IO dispatcher.
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
     * Caches the provided JSON string in SharedPreferences for offline use.
     */
    private fun cacheJson(context: Context, json: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_JSON, json)
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
     * Parses a JSON string into a PlaylistsResponse object using Gson.
     *
     * @return A PlaylistsResponse object, or null if parsing fails.
     */
    private fun parseJson(json: String): PlaylistsResponse? {
        return try {
            // This type token tells Gson to parse into our top-level PlaylistsResponse object
            val type = object : TypeToken<PlaylistsResponse>() {}.type
            Gson().fromJson<PlaylistsResponse>(json, type)
        } catch (e: Exception) {
            println("❌ Playlist JSON parsing failed: ${e.message}")
            null
        }
    }

    /**
     * Clears the playlist cache from SharedPreferences. Can be used for a "force refresh".
     */
    fun clearCache(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_JSON)
            .apply()
        println("🧹 Playlist cache cleared.")
    }
}
