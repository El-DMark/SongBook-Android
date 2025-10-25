package com.paam.songbook

import com.paam.songbook.model.LyricsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/**
 * Local unit tests for LyricsRepository.
 */
class LyricsRepositoryTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

//    @Test
//    fun fetchSongById_returnsLyrics() = runBlocking {
//        val repo = LyricsRepository()
//        val lyrics = repo.fetchSongById("1") // test with song id 1
//
//        assertNotNull("Lyrics should not be null", lyrics)
//        assertEquals("1", lyrics?.id)
//        println("Fetched lyrics: ${lyrics?.chorus}")
//    }
}
