package com.paam.songbook.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paam.songbook.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(songs: List<Song>, navController: NavController) {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1F1C2C), Color(0xFF928DAB))
                )
            )
            .padding(16.dp)
    ) {
        // Glassmorphism Search Bar
        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search songs...", color = Color.LightGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
               // .blur(8.dp)
                .background(Color.White.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedTextColor = Color.White,
                cursorColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        val filteredSongs = songs.filter {
            query.isBlank() || it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredSongs.size) { index ->
                val song = filteredSongs[index]
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
