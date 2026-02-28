package com.paam.songbook.ui.fullplayer

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SongInfoSection(
    title: String,
    artist: String,
    textColor: Color,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    progress: Float,
    onDownloadClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔹 Text Column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp) // Add padding so text doesn't touch the icon
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(),
                // 🔹 Change to TextAlign.Start for better balance with the icon on the right
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Visible
            )

            Text(
                text = artist,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(),
                // 🔹 Change to TextAlign.Start
                textAlign = TextAlign.Start
            )
        }

        // 🔹 Download Action Area
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isDownloading) {
                // 🔹 Live Progress Circle
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(28.dp),
                    color = Color(0xFF4CAF50), // Green
                    strokeWidth = 3.dp,
                    trackColor = textColor.copy(alpha = 0.2f)
                )
            } else {
                // 🔹 Reactive Icon Button
                IconButton(
                    onClick = { if (!isDownloaded) onDownloadClick() }
                ) {
                    Icon(
                        imageVector = if (isDownloaded) Icons.Default.DownloadDone else Icons.Outlined.Download,
                        contentDescription = "Download",
                        // 🔹 This is the critical line for the "Live" update
                        tint = if (isDownloaded) Color(0xFF4CAF50) else textColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}