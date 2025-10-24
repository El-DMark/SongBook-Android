package com.paam.songbook.ui.artists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paam.songbook.model.Song

@Composable
fun ArtistListScreen(
    songs: List<Song>,
    onArtistSelected: (String) -> Unit
) {
    val artists = songs
        .groupBy { it.artist }
        .map { (artist, artistSongs) ->
            ArtistUiModel(
                name = artist,
                songCount = artistSongs.size
            )
        }
        .sortedBy { it.name }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(artists) { artist ->
            ListItem(
                headlineContent = {
                    Text(
                        text = artist.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                supportingContent = {
                    Text(
                        text = "${artist.songCount} songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                },
                leadingContent = {
                    // 🔹 Default circular character icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                // pick a soft color based on hash of name
                                colorForArtist(artist.name)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = artist.name.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onArtistSelected(artist.name) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }
    }
}

private data class ArtistUiModel(
    val name: String,
    val songCount: Int
)

// 🔹 Simple function to generate a consistent background color per artist
@Composable
private fun colorForArtist(name: String): Color {
    val colors = listOf(
        Color(0xFFEF5350), // red
        Color(0xFFAB47BC), // purple
        Color(0xFF42A5F5), // blue
        Color(0xFF26A69A), // teal
        Color(0xFF66BB6A), // green
        Color(0xFFFFCA28)  // amber
    )
    val index = (name.hashCode().absoluteValue % colors.size)
    return colors[index]
}

private val Int.absoluteValue: Int
    get() = if (this < 0) -this else this
