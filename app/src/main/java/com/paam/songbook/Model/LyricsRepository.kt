package com.paam.songbook.model

import Lyrics
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object LyricsRepository {
    private const val PREF_NAME = "lyrics_cache"
    private const val KEY_JSON = "cached_json"

    suspend fun loadLyrics(
        context: Context,
        jsonUrl: String,
        useCacheOnly: Boolean = false
    ): List<Lyrics> {
        return try {
            val json = if (useCacheOnly) {
                getCachedJson(context) ?: throw Exception("No cached data available")
            } else {
                val fetched = fetchJson(jsonUrl)
                cacheJson(context, fetched)
                fetched
            }
            parseJson(json)
        } catch (e: Exception) {
            println("⚠️ Failed to load lyrics from network: ${e.message}")
            getCachedJson(context)?.let {
                println("📦 Using cached lyrics")
                parseJson(it)
            } ?: run {
                println("❌ No cached lyrics available")
                emptyList()
            }
        }
    }

    private suspend fun fetchJson(url: String): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        println("🧪 Lyrics Content-Type: ${response.header("Content-Type")}")

        if (!response.isSuccessful) {
            throw Exception("HTTP error: ${response.code}")
        }

        response.body?.string() ?: throw Exception("Empty response body")
    }

    private fun cacheJson(context: Context, json: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_JSON, json)
            .apply()
    }

    fun clearCache(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_JSON)
            .apply()
    }

    private fun getCachedJson(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_JSON, null)
    }

    private fun parseJson(json: String): List<Lyrics> {
        return try {
            val type = object : TypeToken<List<Lyrics>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            println("❌ Lyrics JSON parsing failed: ${e.message}")
            emptyList()
        }
    }
}
