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
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onSongClicked() }
    ) {
        // 1. Album Art
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = song.albumArt,
                contentDescription = song.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Song Title
        Text(
            text = song.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        // 3. Artist Text
        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
