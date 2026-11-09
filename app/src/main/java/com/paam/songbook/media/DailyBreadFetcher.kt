package com.paam.songbook.media

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.time.LocalDate

// Data class to hold the verse
data class VerseData(
    val reference: String,
    val text: String
)

class DailyBreadFetcher(private val context: Context) {

    suspend fun fetchVerse(): VerseData? = withContext(Dispatchers.IO) {
        val sharedPrefs: SharedPreferences = context.getSharedPreferences("daily_bread_prefs", Context.MODE_PRIVATE)
        val cachedDate = sharedPrefs.getString("cached_date", "")
        val today = LocalDate.now().toString()

        // 1. Return cached verse instantly if it's from today
        if (cachedDate == today) {
            val cachedReference = sharedPrefs.getString("cached_reference", null)
            val cachedText = sharedPrefs.getString("cached_text", null)
            if (cachedReference != null && cachedText != null) {
                return@withContext VerseData(cachedReference, cachedText)
            }
        }

        // 2. Otherwise, fetch from network
        try {
            val doc = Jsoup.connect("https://branham.org/en/quoteoftheday").get()

            // Use ID selectors (#) instead of class selectors (.)
            val reference = doc.selectFirst("#scripturereference")?.text()
            val text = doc.selectFirst("#scripturetext")?.text()

            if (reference != null && text != null) {
                // Save to cache
                sharedPrefs.edit().apply {
                    putString("cached_date", today)
                    putString("cached_reference", reference)
                    putString("cached_text", text)
                    apply()
                }
                VerseData(reference = reference, text = text)
            } else {
                fallbackToCache(sharedPrefs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 3. Fallback to older cached verse if network fails
            fallbackToCache(sharedPrefs)
        }
    }

    private fun fallbackToCache(sharedPrefs: SharedPreferences): VerseData? {
        val cachedReference = sharedPrefs.getString("cached_reference", null)
        val cachedText = sharedPrefs.getString("cached_text", null)
        return if (cachedReference != null && cachedText != null) {
            VerseData(cachedReference, cachedText)
        } else {
            null
        }
    }
}
