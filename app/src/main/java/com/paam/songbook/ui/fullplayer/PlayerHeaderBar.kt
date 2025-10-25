package com.paam.songbook.ui.fullplayer

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerHeaderBar(
    onCollapse: () -> Unit,
    showLyrics: Boolean,
    matchedLyricsAvailable: Boolean,
    onToggleLyrics: () -> Unit,
    tint: Color // ✅ new
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp)
    ) {
        IconButton(
            onClick = onCollapse,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ExpandMore, contentDescription = "Collapse", tint = tint)
        }

        if (matchedLyricsAvailable) {
            IconButton(
                onClick = onToggleLyrics,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (showLyrics) Icons.Default.Close else Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = if (showLyrics) "Hide Lyrics" else "Show Lyrics",
                    tint = tint
                )
            }
        }
    }
}


