package com.paam.songbook.ui.components

import Lyrics
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paam.songbook.Model.Song
import com.paam.songbook.ui.fullplayer.FullPlayer
import kotlinx.coroutines.launch

enum class PlayerState { Mini, Full }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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

    // ✅ Always keep scaffold mounted
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.navigationBars.asPaddingValues())
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = if (currentSong == null) 0.dp else 72.dp, // collapse when no song
            sheetDragHandle = {},
            sheetContainerColor = Color.Transparent,   // no background behind mini player
            sheetContent = {
                if (currentSong != null) {
                    Box(modifier = Modifier.animateContentSize()) {
                        Crossfade(targetState = playerState, label = "PlayerCrossfade") { state ->
                            when (state) {
                                PlayerState.Mini -> {
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = true,
                                        modifier = Modifier.align(Alignment.BottomCenter),
                                        enter = slideInVertically(
                                            initialOffsetY = { it },
                                            animationSpec = tween(400)
                                        ) + fadeIn(animationSpec = tween(400)),
                                        exit = slideOutVertically(
                                            targetOffsetY = { it },
                                            animationSpec = tween(300)
                                        ) + fadeOut(animationSpec = tween(300))
                                    ) {
                                        Surface(
                                            tonalElevation = 0.dp,
                                            shadowElevation = 0.dp,
                                            color = Color.Transparent,
                                            shape = RoundedCornerShape(16.dp), // ✅ rounded corners
                                            modifier = Modifier
                                                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                                                .fillMaxWidth()
                                        ) {
                                            MiniPlayer(
                                                currentSong = currentSong,
                                                isPlaying = isPlaying,
                                                position = position,
                                                duration = duration,
                                                onExpand = { scope.launch { scaffoldState.bottomSheetState.expand() } },
                                                onPlayPause = onPlayPause,
                                                onNext = onNext,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
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
                                    lyrics = lyrics
                                )
                            }
                        }
                    }
                }
            }
        ) { content() }
    }
}
