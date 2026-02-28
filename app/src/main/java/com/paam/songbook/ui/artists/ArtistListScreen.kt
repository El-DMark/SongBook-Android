package com.paam.songbook.ui.artists

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paam.songbook.model.Song
import kotlin.math.absoluteValue

@Composable
fun ArtistListScreen(
    songs: List<Song>,
    onArtistSelected: (String) -> Unit
) {
    val artists = remember(songs) {
        songs
            .filter { it.artist.isNotBlank() }
            .groupBy { it.artist }
            .map { (artist, artistSongs) ->
                ArtistUiModel(name = artist, songCount = artistSongs.size)
            }
            .sortedBy { it.name }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            )
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, top = 16.dp, bottom = 80.dp), // Extra bottom padding for Player
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            itemsIndexed(artists) { index, artist ->
                // Adding an entrance animation
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { visible = true }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = index * 50)) +
                            slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(500, delayMillis = index * 50))
                ) {
                    ArtistGridItem(
                        artist = artist,
                        onArtistSelected = { onArtistSelected(artist.name) }
                    )
                }
            }
        }
    }
}


@Composable
private fun ArtistGridItem(
    artist: ArtistUiModel,
    onArtistSelected: () -> Unit
) {
    val gradientBrush = remember(artist.name) {
        Brush.sweepGradient(colorForArtist(artist.name))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onArtistSelected() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Modern Circular Avatar with Glow
        Box(
            modifier = Modifier
                .size(110.dp)
                .shadow(12.dp, CircleShape, spotColor = colorForArtist(artist.name).first())
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
                .border(BorderStroke(2.dp, Color.White.copy(alpha = 0.2f)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Inner colored circle
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(brush = gradientBrush),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = artist.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 32.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Focused Typography
        Text(
            text = artist.name,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            textAlign = TextAlign.Center,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Text(
            text = if (artist.songCount == 1) "1 Hymn" else "${artist.songCount} Hymns",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.6f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

private data class ArtistUiModel(
    val name: String,
    val songCount: Int
)


private fun colorForArtist(name: String): List<Color> {
    val colorPairs = listOf(
        listOf(Color(0xFFFF5F6D), Color(0xFFFFC371)), // Sunset
        listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)), // Ocean
        listOf(Color(0xFFee9ca7), Color(0xFFffdde1)), // Rose
        listOf(Color(0xFF06beb6), Color(0xFF48b1bf)), // Emerald
        listOf(Color(0xFF6441A5), Color(0xFF2a0845)), // Purple Night
        listOf(Color(0xFFFDC830), Color(0xFFF37335))  // Orange Glow
    )
    val index = (name.hashCode().absoluteValue % colorPairs.size)
    return colorPairs[index]
}