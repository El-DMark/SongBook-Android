package com.paam.songbook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.paam.songbook.model.Song

@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(160.dp)
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        // Album art
        Image(
            painter = rememberAsyncImagePainter(song.albumArt),
            contentDescription = song.title,
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Title
        Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Artist
        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}
