package com.paam.songbook.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Songs Of Bride", style = MaterialTheme.typography.headlineSmall)
            Text("Version 1.0.0", style = MaterialTheme.typography.bodyMedium)

            Divider()

            Text(
                "This app is built to provide a seamless music experience with secure streaming, " +
                        "offline support, and a modern Material You design."
            )

            Divider()

            Text("Credits", style = MaterialTheme.typography.titleMedium)
            Text("• Developed by Mark David\n• Powered by AndroidX Media3 & Jetpack Compose")

            Divider()

            Text("Acknowledgments", style = MaterialTheme.typography.titleMedium)
            Text("Special thanks to the open-source community and contributors.")

            Divider()

            Text("Legal", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { /* TODO: open Privacy Policy */ }) {
                Text("Privacy Policy")
            }
            TextButton(onClick = { /* TODO: open Terms of Service */ }) {
                Text("Terms of Service")
            }
        }
    }
}
