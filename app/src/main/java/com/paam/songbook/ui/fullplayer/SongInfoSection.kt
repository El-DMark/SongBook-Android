package com.paam.songbook.ui.fullplayer

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SongInfoSection(title: String, artist: String, textColor: Color) {
    Text(title, style = MaterialTheme.typography.titleLarge, color = textColor)
    Text(artist, style = MaterialTheme.typography.bodyMedium, color = textColor.copy(alpha = 0.8f))
}
