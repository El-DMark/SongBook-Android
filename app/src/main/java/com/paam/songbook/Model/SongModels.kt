package com.paam.songbook.model

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request

data class Song(
    val songID: String,
    val title: String,
    val artist: String,
    val url: String,
    val albumArt: String,
    val lyrics: String?,
    val Language: String,      // e.g. "Hindi", "English"
    val timestamp: String
)


//private const val SONG_CACHE_PREF = "song_cache"
//private const val SONG_JSON_KEY = "cached_json"
//
///**
// * Fetch songs from Google Drive JSON and cache locally.
// */
//suspend fun fetchSongsFromDrive(context: Context, jsonUrl: String): List<Song> {
//    return try {
//        val client = OkHttpClient()
//        val request = Request.Builder().url(jsonUrl).build()
//        val response = client.newCall(request).execute()
//        val json = response.body?.string() ?: return sampleSongs()
//
//        cacheJsonLocally(context, json)
//        parseSongs(json)
//    } catch (e: Exception) {
//        getCachedJson(context)?.let { parseSongs(it) } ?: sampleSongs()
//    }
//}
//
///**
// * Parse JSON into Song list.
// */
//private fun parseSongs(json: String): List<Song> {
//    val gson = Gson()
//    val type = object : TypeToken<List<Song>>() {}.type
//    return gson.fromJson(json, type)
//}
//
///**
// * Cache JSON string locally.
// */
//private fun cacheJsonLocally(context: Context, json: String) {
//    context.getSharedPreferences(SONG_CACHE_PREF, Context.MODE_PRIVATE)
//        .edit()
//        .putString(SONG_JSON_KEY, json)
//        .apply()
//}
//
///**
// * Retrieve cached JSON string.
// */
//private fun getCachedJson(context: Context): String? {
//    return context.getSharedPreferences(SONG_CACHE_PREF, Context.MODE_PRIVATE)
//        .getString(SONG_JSON_KEY, null)
//}
//
///**
// * Fallback sample songs.
// */
//fun sampleSongs(): List<Song> = listOf(
//    Song(
//        1,
//        "Snow White Dove",
//        "Bro. Lal",
//        "https://drive.google.com/uc?export=download&id=1tkXoNuMh33ZqBrrOMiKFa8GWZ0onDWT3",
//        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg",
//        "Lyrics not available"
//    ),
//    Song(
//        2,
//        "As The Deer",
//        "Bro. Nischal",
//        "https://drive.google.com/uc?export=download&id=1T3-MiEJQ5E2vK2uiI5TFlp_vjVfURGL8",
//        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg",
//        "Lyrics not available"
//    ),
//    Song(
//        3,
//        "Abba Father",
//        "Bro. Ashish",
//        "https://drive.google.com/uc?export=download&id=1AbGD7hYC1j9p09EaQzpeFjvLIQ_B7gDi",
//        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg",
//        "Lyrics not available"
//    ),
//    Song(
//        4,
//        "I Want To Go",
//        "Bro. Vin Dayal",
//        "https://drive.google.com/uc?export=download&id=1-YGY5T2tJQhlxvSoOV9-7aJue7JrDj-m",
//        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg",
//        "Lyrics not available"
//    ),
//    Song(
//        5,
//        "My Faith Look Up To Thee",
//        "Bro. Branham",
//        "https://drive.google.com/uc?export=download&id=1Sw_Bdg7AH6eKREKr6WB3PUdDUyc9N9Az",
//        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg",
//        "Lyrics not available"
//    ),
//    Song(
//        6,
//        "गर चाहते छूटना गुनाहों से तुम लहू में कुदरत है",
//        "Bro. Nischal",
//        "https://drive.google.com/uc?export=download&id=1NHaDHQlDXBWwkAuH60XLXnOhyyjm2G79",
//        "https://i0.wp.com/endtimesmessages.com/wp-content/uploads/2019/03/The-Supernatural-Cloud-1963.jpg",
//        "Lyrics not available"
//    )
//)

/**
 * Convert Song to MediaItem for ExoPlayer.
 */
fun Song.toMediaItem(): MediaItem =
    MediaItem.Builder()
        .setUri(url)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setArtworkUri(Uri.parse(albumArt))
                .build()
        )
        .build()