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
            TopAppBar(
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
            Text("App Version 1.0.0", style = MaterialTheme.typography.bodyMedium)
            Text("Song Repo Version 1.0.0", style = MaterialTheme.typography.bodyMedium)

            Divider()

            Text(
                "This app is built to provide a seamless and Ad free listening of songs sang by Brothers and Sister in Christ, " +
                        "This app is dedicated to Bride of Christ"
            )

            Divider()

            Text("Credits", style = MaterialTheme.typography.titleMedium)
            Text("• Developed by Mark David\n• Song repository curated by Varsha David")

 //           Divider()

//            Text("Acknowledgments", style = MaterialTheme.typography.titleMedium)
//            Text("Songs and their lyrics are not belong to me")

//            Divider()
//
//            Text("Legal", style = MaterialTheme.typography.titleMedium)
//            TextButton(onClick = { /* TODO: open Privacy Policy */ }) {
//                Text("Privacy Policy")
//            }
//            TextButton(onClick = { /* TODO: open Terms of Service */ }) {
//                Text("Terms of Service")
//            }
        }
    }
}
