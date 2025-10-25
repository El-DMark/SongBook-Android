package com.paam.songbook.ui.fullplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.valentinilk.shimmer.shimmer

@Composable
fun AlbumArtDisplay(
    albumArt: String,
    title: String,
    albumSize: Dp,
    albumAlpha: Float
) {
    SubcomposeAsyncImage(
        model = albumArt,
        contentDescription = title,
        modifier = Modifier
            .size(albumSize)
            .alpha(albumAlpha)
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                modifier = Modifier
                    .size(albumSize)
                    .clip(RoundedCornerShape(12.dp))
                    .shimmer()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        error = {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Fallback",
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}
