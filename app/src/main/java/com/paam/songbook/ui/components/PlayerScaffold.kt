package com.paam.songbook.ui.components

import Lyrics
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paam.songbook.model.Song
import kotlinx.coroutines.launch

enum class PlayerState { Mini, Full }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScaffold(
    currentSong: Song?,
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    lyrics: List<Lyrics>,
    content: @Composable () -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    val playerState by remember {
        derivedStateOf {
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) PlayerState.Full
            else PlayerState.Mini
        }
    }

    // ✅ If no song is active, just show the content without any player
    if (currentSong == null) {
        content()
    } else {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 72.dp,
            sheetDragHandle = {},
            modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
            sheetContent = {
                Box(modifier = Modifier.animateContentSize()) {
                    Crossfade(targetState = playerState, label = "PlayerCrossfade") { state ->
                        when (state) {
                            PlayerState.Mini -> MiniPlayer(
                                currentSong = currentSong,
                                isPlaying = isPlaying,
                                position = position,
                                duration = duration,
                                onExpand = { scope.launch { scaffoldState.bottomSheetState.expand() } },
                                onPlayPause = onPlayPause,
                                onNext = onNext
                            )
                            PlayerState.Full -> FullPlayer(
                                currentSong = currentSong,
                                isPlaying = isPlaying,
                                position = position,
                                duration = duration,
                                onCollapse = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } },
                                onPlayPause = onPlayPause,
                                onNext = onNext,
                                onPrevious = onPrevious,
                                onSeek = onSeek,
                                lyrics=lyrics

                            )
                        }
                    }
                }
            }
        ) {
            content()
        }
    }
}
