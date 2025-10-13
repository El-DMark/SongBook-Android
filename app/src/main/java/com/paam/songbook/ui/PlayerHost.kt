package com.paam.songbook.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.paam.songbook.model.Song
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerHost(
    controller: MediaController,
    songs: List<Song>,
    isExpanded: Boolean,
    onExpand: () -> Unit, // ✅ Correct parameter name
    content: @Composable (Modifier) -> Unit
) {
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 72.dp,
        sheetContent = {
            UnifiedPlayer(
                controller = controller,
                isExpanded = sheetState.bottomSheetState.currentValue == SheetValue.Expanded,
                songs = songs,
                onExpand = { scope.launch { sheetState.bottomSheetState.expand() } } // ✅ triggers expansion
            )

        },
        sheetDragHandle = { } // ✅ Empty composable removes the hinge
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content(Modifier)
        }
    }
}

