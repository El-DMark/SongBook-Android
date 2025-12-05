package com.paam.songbook.ui.artists

// --- 1. ADD BorderStroke import ---
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paam.songbook.model.Song
import kotlin.math.absoluteValue

@Composable
fun ArtistListScreen(
    songs: List<Song>,
    onArtistSelected: (String) -> Unit
) {
    val artists = songs
        .filter { it.artist.isNotBlank() }
        .groupBy { it.artist }
        .map { (artist, artistSongs) ->
            ArtistUiModel(
                name = artist,
                songCount = artistSongs.size
            )
        }
        .sortedBy { it.name }

    // --- 2. ADD a wrapping Box with a gradient background ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF2C5364)
                    )
                )
            )
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            // Increase padding for the new look
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(artists) { artist ->
                ArtistGridItem(
                    artist = artist,
                    onArtistSelected = { onArtistSelected(artist.name) }
                )
            }
        }
    }
}

@Composable
private fun ArtistGridItem(
    artist: ArtistUiModel,
    onArtistSelected: () -> Unit
) {
    // --- 3. APPLY GLASSMORPHISM to the Card ---
    Card(
        modifier = Modifier
            // Remove padding from here as it's now in the grid
            .clickable { onArtistSelected() },
        // Use more rounded corners for the glass look
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            // Key part of the effect: semi-transparent white
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        // Glass doesn't have shadows
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        // Add a subtle border to define the glass edge
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // Adjust padding for the new card style
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val gradientBrush = Brush.linearGradient(
                colors = colorForArtist(artist.name)
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(brush = gradientBrush),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = artist.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 4. UPDATE Typography for readability ---
            Text(
                text = artist.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = Color.White // Set text to white
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${artist.songCount} songs",
                style = MaterialTheme.typography.bodySmall,
                // Use a slightly transparent white for secondary text
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}


private data class ArtistUiModel(
    val name: String,
    val songCount: Int
)

@Composable
private fun colorForArtist(name: String): List<Color> {
    val colorPairs = listOf(
        Color(0xFFF44336) to Color(0xFFFF9800),
        Color(0xFF9C27B0) to Color(0xFFE91E63),
        Color(0xFF2196F3) to Color(0xFF03A9F4),
        Color(0xFF009688) to Color(0xFF4CAF50),
        Color(0xFF3F51B5) to Color(0xFF42A5F5),
        Color(0xFFFF5722) to Color(0xFFFFC107)
    )
    val index = (name.hashCode().absoluteValue % colorPairs.size)
    val pair = colorPairs[index]
    return listOf(pair.first, pair.second)
}

