package com.paam.songbook.ui.fullplayer

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun SongInfoSection(title: String, artist: String, textColor: Color) {
    // ✅ Title with marquee effect
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = textColor,
        modifier = Modifier
            .fillMaxWidth()
            .basicMarquee(), // enables marquee scrolling
        textAlign = TextAlign.Center,
        maxLines = 1, // single line only
        overflow = TextOverflow.Visible // allow marquee instead of ellipsis
    )

    // ✅ Artist stays normal
    Text(
        text = artist,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}
