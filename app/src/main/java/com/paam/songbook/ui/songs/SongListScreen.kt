package com.paam.songbook.ui.songs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import coil.compose.rememberAsyncImagePainter
import com.paam.songbook.model.Song

@Composable
fun SongListScreen(
    songs: List<Song>,
    onSongSelected: (Song) -> Unit
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = bottomInset + 72.dp // add player height
        )
    ) {
        items(songs) { song ->
            ListItem(
                headlineContent = {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.bodyMedium//.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                supportingContent = {
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                },
                leadingContent = {
                    if (!song.albumArt.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(song.albumArt),
                            contentDescription = song.title,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        // fallback placeholder
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onSongSelected(song) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                        }
                        IconButton(onClick = { /* TODO: show menu */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSongSelected(song) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }
    }
}
