package com.paam.songbook.ui.songs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
// --- 1. ADD clickable import ---
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// --- 2. ADD MediaController and MediaItem imports ---
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import coil.compose.AsyncImage
import com.paam.songbook.media.DailyBreadFetcher
import com.paam.songbook.media.VerseData
import com.paam.songbook.model.Song
import com.paam.songbook.media.toMediaItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@OptIn(ExperimentalFoundationApi::class)
@Composable
// --- 3. ACCEPT MediaController as a parameter ---
fun FeaturedSongCard(song: Song, controller: MediaController?) {
    // We can have more slides in the future
    val pageCount = 2
    val pagerState = rememberPagerState(pageCount = { pageCount })

    // Auto-scrolling coroutine
    LaunchedEffect(Unit) {
        while(true) {
            delay(5000) // Wait for 5 seconds on the current page
            yield()
            val nextPage = (pagerState.currentPage + 1) % pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                // Use a when statement to build different slides
                when (page) {
                    // --- 4. PASS controller down to the ImageSlide ---
                    0 -> ImageSlide(song = song, controller = controller)
                    1 -> TextSlide()
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Page indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
// --- 5. ACCEPT controller in ImageSlide ---
private fun ImageSlide(song: Song, controller: MediaController?) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // --- 6. ADD the clickable modifier ---
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Disable ripple effect for a clean click
                onClick = {
                    controller?.let {
                        it.clearMediaItems() // Clear previous playlist
                        it.addMediaItem(song.toMediaItem()) // Add only this song
                        it.prepare()
                        it.play()
                    }
                }
            ),
        contentAlignment = Alignment.BottomStart
    ) {
        // Album art as the background image
        AsyncImage(
            model = song.albumArt,
            contentDescription = song.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        startY = 80f
                    )
                )
        )

        // Use a Column to stack the title and artist name vertically.
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Song title
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            // Artist name
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f) // Slightly transparent for secondary info
            )
        }
    }
}

@Composable
private fun TextSlide() {
    var verseData by remember { mutableStateOf<VerseData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        verseData = DailyBreadFetcher().fetchVerse()
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        Color.Black
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            when {
                isLoading -> {
                    Text(
                        text = "Loading verse...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
                verseData != null -> {
                    Text(
                        text = "\"${verseData!!.text}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = verseData!!.reference,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                else -> {
                    Text(
                        text = "Could not load verse.\nPlease check your connection.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
