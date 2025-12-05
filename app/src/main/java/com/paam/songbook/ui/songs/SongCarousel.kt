package com.paam.songbook.ui.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.border // 👈 IMPORT for the border modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.session.MediaController
import coil.compose.AsyncImage
import com.paam.songbook.media.toMediaItem
import com.paam.songbook.model.Song

/**
 * A horizontal, lazily-loaded scrolling list of songs.
 */
@Composable
fun SongCarousel(
    songs: List<Song>,
    controller: MediaController,
    modifier: Modifier = Modifier
) {
    // 1. Switched to LazyRow for better performance
    LazyRow(
        modifier = modifier,
        // Use contentPadding for consistent spacing at the start and end
        contentPadding = PaddingValues(horizontal = 6.dp),
        // Arrangement.spacedBy handles the spacing between items
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Use the modern items(list) { item -> ... } syntax
        items(songs) { song ->
            // 2. Replaced SongTile with the new, enhanced SongCarouselItem
            SongCarouselItem(
                song = song,
                onSongClicked = {
                    // Logic to play the selected song within the context of the carousel's list
                    val mediaItems = songs.map { it.toMediaItem() }
                    val startIndex = songs.indexOf(song).coerceAtLeast(0)
                    controller.setMediaItems(mediaItems, startIndex, 0L)
                    controller.prepare()
                    controller.play()
                }
            )
        }
    }
}

/**
 * An individual item in the SongCarousel, featuring album art and overlayed text.
 * This is private to this file as it's only used by SongCarousel.
 */
@Composable
private fun SongCarouselItem(
    song: Song,
    onSongClicked: () -> Unit
) {
    val cardShape = RoundedCornerShape(12.dp) // Define shape once for reuse
    Box(
        modifier = Modifier
            // --- 1. SIZE REDUCED ---
            .width(120.dp)
            .height(160.dp)
            .clip(cardShape) // Clip the content inside
            // --- 2. BORDER ADDED ---
            // Add a border that follows the clipped shape
            .border(1.dp, Color.White.copy(alpha = 0.5f), cardShape)
            .clickable { onSongClicked() }
            .background(MaterialTheme.colorScheme.surfaceVariant), // Fallback color
        contentAlignment = Alignment.BottomStart // Aligns text to the bottom-left
    ) {
        //  Album Art as Background
        AsyncImage(
            model = song.albumArt,
            contentDescription = song.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        //  Play Icon (Center with transparency)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    // --- 3. ICON SIZE REDUCED ---
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)), // Semi-transparent background
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        //  Gradient Overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                        startY = 250f // Adjusted gradient start
                    )
                )
        )

        //  Song Name and Artist at the bottom
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp), // Slightly reduced padding
            verticalArrangement = Arrangement.Bottom // Pushes the text to the bottom
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), // Made artist text smaller
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
