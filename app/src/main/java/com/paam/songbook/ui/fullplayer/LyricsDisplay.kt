package com.paam.songbook.ui.fullplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LyricsDisplay(
    fetchedLyrics: String?,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f) // ✅ always 60% of screen height
            .background(Color.Transparent)
    ) {
        val lyricBlocks = fetchedLyrics?.split("\n\n")?.filter { it.isNotBlank() } ?: emptyList()
        
        // Premium top & bottom fade mask
        val fadeBrush = Brush.verticalGradient(
            0f to Color.Transparent,
            0.15f to Color.Black,
            0.85f to Color.Black,
            1f to Color.Transparent
        )

        if (lyricBlocks.isEmpty()) {
            Text(
                text = "Loading lyrics...",
                fontSize = 20.sp,
                color = textColor,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 24.dp)
                    .graphicsLayer { alpha = 0.99f } // Forces offscreen buffer for DstIn blend mode
                    .drawWithContent {
                        drawContent()
                        drawRect(brush = fadeBrush, blendMode = BlendMode.DstIn)
                    },
                contentPadding = PaddingValues(vertical = 64.dp), // Extra padding to allow scrolling past the fade
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                items(lyricBlocks) { block ->
                    val cleanedText = block.lines()
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .joinToString("\n")

                    if (cleanedText.isNotEmpty()) {
                        Text(
                            text = cleanedText,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                lineHeight = 44.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = textColor.copy(alpha = 0.9f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}
