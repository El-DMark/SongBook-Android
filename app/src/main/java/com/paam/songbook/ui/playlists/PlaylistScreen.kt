package com.paam.songbook.ui.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.paam.songbook.model.Playlist

/**
 * A screen that displays playlists in a visually rich grid.
 */
@Composable
fun PlaylistScreen(
    playlists: List<Playlist>,
    onPlaylistSelected: (Playlist) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(playlists) { playlist ->
            PlaylistItem(
                playlist = playlist,
                onPlaylistSelected = { onPlaylistSelected(playlist) }
            )
        }
    }
}

/**
 * A single card representing a playlist in the grid.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onPlaylistSelected: () -> Unit
) {
    // Check if this is the special Favorites playlist
    val isFavorites = playlist.name.equals("Favorites", ignoreCase = true)

    Card(
        onClick = onPlaylistSelected,
        modifier = Modifier.padding(8.dp),
        // HIGHLIGHT: Highlight Favorites with a different container color
        colors = if (isFavorites) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // Ensures the image area is a square
                contentAlignment = Alignment.Center
            ) {
                // 1. Logic for Favorites Gradient Placeholder
                if (isFavorites && playlist.coverArtUrl.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }
                } else {
                    // --- PRESERVED PREVIOUS CRASH FIX ---
                    // 1. Create a painter for the fallback/error icon
                    val errorPainter = rememberVectorPainter(image = Icons.Default.MusicNote)

                    // 2. Create the main image painter with a fallback
                    val imagePainter = rememberAsyncImagePainter(
                        model = playlist.coverArtUrl,
                        error = errorPainter, // Use the painter for errors
                        fallback = errorPainter // Also use for empty model
                    )

                    // 3. Use the standard Image composable with the new painter
                    Image(
                        painter = imagePainter,
                        contentDescription = playlist.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Info section: Name and song count
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = playlist.name,
                    // Use ExtraBold weight for Favorites
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isFavorites) FontWeight.ExtraBold else FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    // Use onPrimaryContainer color for Favorites
                    color = if (isFavorites) MaterialTheme.colorScheme.onPrimaryContainer else Color.Unspecified
                )
                // Display the number of songs from the 'songs' list
                Text(
                    text = "${playlist.songs.size} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFavorites) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}