package com.paam.songbook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import com.paam.songbook.model.Song

@Composable
fun AlbumArt(
    song: Song,
    size: Dp,
    cornerRadius: Dp
) {
    Image(
        painter = rememberAsyncImagePainter(song.albumArt),
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius)),
        contentScale = ContentScale.Crop
    )
}
