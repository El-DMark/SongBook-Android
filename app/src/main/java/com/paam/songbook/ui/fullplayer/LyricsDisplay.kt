package com.paam.songbook.ui.fullplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LyricsDisplay(
    fetchedLyrics: String?,
    textColor: Color // ✅ new parameter
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp, max = 500.dp)// flexible height
            .clip(MaterialTheme.shapes.medium)
            .background(Color.Transparent) // let gradient/background show through
    ) {
        // Scrollable lyrics text
        Box(
            modifier = Modifier
                .matchParentSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = fetchedLyrics ?: "Loading lyrics...",
                style = MaterialTheme.typography.bodyLarge,
                color = textColor, // ✅ adaptive tint,
                modifier =Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // 🔹 Gradient overlay at bottom for readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.4f) // subtle fade
                        )
                    )
                )
        )
    }
}
