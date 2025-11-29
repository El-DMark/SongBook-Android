package com.paam.songbook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.paam.songbook.model.Song

@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        if (song.albumArt.isBlank()) {
            // Show music icon if no URL
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Music note",
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(24.dp), // center the icon
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            // Try to load album art
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(song.albumArt)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = song.title,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = song.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Visible, // important for marquee
            modifier = Modifier.basicMarquee()

        )

        Spacer(modifier = Modifier.height(2.dp))

//        Text(
//            text = song.artist,
//            style = MaterialTheme.typography.bodySmall,
//            maxLines = 1
//        )
    }
}
