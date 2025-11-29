package com.paam.songbook.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paam.songbook.model.Song

@Composable
fun FavoritesScreen(songs: List<Song>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F2027), Color(0xFF2C5364))
                )
            )
            .padding(16.dp)
    ) {
        Text("Favorites", style = MaterialTheme.typography.headlineLarge, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        if (songs.isEmpty()) {
            Text("No favorites yet. Add some songs!", color = Color.LightGray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(songs.size) { index ->
                    val song = songs[index]
                    Card(
                        onClick = { navController.navigate("songDetail/${song.songID}") },
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(song.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text(song.artist, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}
