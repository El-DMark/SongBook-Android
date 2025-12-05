package com.paam.songbook.ui
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.media3.session.MediaController
//import androidx.navigation.NavController
//import com.paam.songbook.model.Song
//import com.paam.songbook.ui.home.HomeScreen
//
///**
// * Host composable for the HomeScreen.
// * Acts as a bridge between MainActivity and the UI layer.
// */
//@Composable
//fun HomeHost(
//    songs: List<Song>,
//    navController: NavController,
//    controller: MediaController,
//    modifier: Modifier = Modifier
//) {
//    // 🔹 Pass controller down to HomeScreen
//    HomeScreen(
//        songs = songs,
//        navController = navController,
//        controller = controller,   // ✅ fixed
//        modifier = modifier
//    )
//}
//
///**
// * Utility function to extract the parent folder name from a file path.
// */
//fun extractFolder(path: String): String {
//    val cleaned = path.removePrefix("file://")
//    val segments = cleaned.split('/', '\\').filter { it.isNotBlank() }
//    return if (segments.size > 1) segments[segments.size - 2] else "Unknown"
//}
