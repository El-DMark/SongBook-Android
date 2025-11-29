    package com.paam.songbook.ui.artists

    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.grid.GridCells
    import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
    import androidx.compose.foundation.lazy.grid.items
    import androidx.compose.foundation.shape.CircleShape
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
            .filter { it.artist.isNotBlank() } // Keep this filter to prevent crashes
            .groupBy { it.artist }
            .map { (artist, artistSongs) ->
                ArtistUiModel(
                    name = artist,
                    songCount = artistSongs.size
                )
            }
            .sortedBy { it.name }

        // Using a LazyVerticalGrid for a more modern, card-based layout
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Displaying 2 artists per row
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(artists) { artist ->
                ArtistGridItem(
                    artist = artist,
                    onArtistSelected = { onArtistSelected(artist.name) }
                )
            }
        }
    }

    @Composable
    private fun ArtistGridItem(
        artist: ArtistUiModel,
        onArtistSelected: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .clickable { onArtistSelected() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // == MODIFICATION START ==
                // Create a gradient brush from our new color function
                val gradientBrush = Brush.linearGradient(
                    colors = colorForArtist(artist.name)
                )

                // Circular icon with the first letter of the artist's name
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        // Use the gradient brush for the background
                        .background(brush = gradientBrush),
                    contentAlignment = Alignment.Center
                ) {
                    // == MODIFICATION END ==
                    Text(
                        text = artist.name.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Artist Name
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Song Count
                Text(
                    text = "${artist.songCount} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }


    private data class ArtistUiModel(
        val name: String,
        val songCount: Int
    )

    // == MODIFICATION START ==
    // Simple function to generate a consistent dual-tone background per artist
    @Composable
    private fun colorForArtist(name: String): List<Color> {
        val colorPairs = listOf(
            // Red to Orange
            Color(0xFFF44336) to Color(0xFFFF9800),
            // Purple to Pink
            Color(0xFF9C27B0) to Color(0xFFE91E63),
            // Blue to Light Blue
            Color(0xFF2196F3) to Color(0xFF03A9F4),
            // Teal to Green
            Color(0xFF009688) to Color(0xFF4CAF50),
            // Indigo to Blue
            Color(0xFF3F51B5) to Color(0xFF42A5F5),
            // Deep Orange to Amber
            Color(0xFFFF5722) to Color(0xFFFFC107)
        )
        val index = (name.hashCode().absoluteValue % colorPairs.size)
        val pair = colorPairs[index]
        return listOf(pair.first, pair.second)
    }
    // == MODIFICATION END ==
