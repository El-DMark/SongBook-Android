package com.paam.songbook.ui.songdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paam.songbook.Model.Song

@Composable
fun SongDetailScreen(song: Song) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFD194), Color(0xFF70E1F5))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(song.title, style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            song.lyrics?.let { Text(it, style = MaterialTheme.typography.bodyLarge, color = Color.White) }
        }

        // Floating toolbar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            IconButton(onClick = { /* Add to favorites */ }) {
                Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = Color.White)
            }
            IconButton(onClick = { /* Share */ }) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
            }
            IconButton(onClick = { /* Toggle dark mode */ }) {
                Icon(Icons.Default.DarkMode, contentDescription = "Dark Mode", tint = Color.White)
            }
        }
    }
}
