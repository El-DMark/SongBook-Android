package com.paam.songbook.ui.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.paam.songbook.model.Song

@Composable
fun FeaturedSongCard(song: Song) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            // Use a simple background as a fallback while the image loads
            .background(MaterialTheme.colorScheme.surfaceVariant),
        // Align content (the title) to the bottom-left corner
        contentAlignment = Alignment.BottomStart
    ) {
        // 1. ADDED ALBUM ART as the background image
        AsyncImage(
            model = song.albumArt,
            contentDescription = song.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Ensures the image fills the card
        )

        // 2. ADDED a gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        // Start the gradient from the middle to the bottom
                        startY = 80f
                    )
                )
        )

        // 3. MOVED the title to the bottom-left and REMOVED the artist
        Text(
            text = song.title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(16.dp) // Add padding to keep it off the edges
        )
    }
}
