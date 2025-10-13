package com.paam.songbook.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.paam.songbook.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerHost(
    controller: MediaController,
    songs: List<Song>,
    isExpanded: Boolean,
    onCollapse: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    BottomSheetScaffold(
        sheetContent = {
            UnifiedPlayer(controller = controller, isExpanded = isExpanded, songs = songs)
        },
        sheetPeekHeight = 72.dp
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content(Modifier)
        }
    }
}