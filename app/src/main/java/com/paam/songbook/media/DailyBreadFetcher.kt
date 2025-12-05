package com.paam.songbook.media

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

// Data class to hold the verse
data class VerseData(
    val reference: String,
    val text: String
)

class DailyBreadFetcher {

    suspend fun fetchVerse(): VerseData? = withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect("https://branham.org/en/quoteoftheday").get()

            // Use ID selectors (#) instead of class selectors (.)
            val reference = doc.selectFirst("#scripturereference")?.text()
            val text = doc.selectFirst("#scripturetext")?.text()

            if (reference != null && text != null) {
                VerseData(reference = reference, text = text)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
