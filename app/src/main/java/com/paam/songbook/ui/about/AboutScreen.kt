package com.paam.songbook.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily.Companion.Cursive
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    // 1. Add a Box with the app's standard gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF2C5364))
                )
            )
    ) {
        Scaffold(
            // 2. Make the Scaffold and TopAppBar transparent
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("About", color = Color.White) }, // 3. Set title color
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White // 3. Set icon color
                            )
                        }
                    },
                    // Use the same colors as in MainScreen
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 4. Adjust text colors for the dark background
                Text(
                    "Tehillah",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontFamily = Cursive,
                    fontSize = 42.sp
                )
                Text(
                    text = "Praise ye the Lord. Sing unto the Lord a new song, and his praise in the congregation of saints.",
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    "Psalm 149:1",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Divider(color = Color.White.copy(alpha = 0.3f))

                Text(
                    text = "Tehillah, Hebrew for a new song of praise, is a collection of anointed worship songs dedicated to our Lord Jesus Christ.",
                    color = Color.White.copy(alpha = 0.9f)
                )

                Divider(color = Color.White.copy(alpha = 0.3f))

                Text(
                    "Credits",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Text(
                    "• App Developed by Mark David\n• " +
                            "Song Repository curated by Varsha David",
                    color = Color.White.copy(alpha = 0.9f)
                )
                Divider(color = Color.White.copy(alpha = 0.3f))
                Text(
                    "Versions",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Text(
                    "App Version 1.0.0\n"
                    +"Song Repo Version 1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f) // Slightly less prominent
                )
                Divider(color = Color.White.copy(alpha = 0.3f))

                Text(
                    "Contacts",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White

                )
                Text(
                    "Email: davidmark275@gmail.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f) // Slightly less prominent
                )
            }
        }
    }
}
