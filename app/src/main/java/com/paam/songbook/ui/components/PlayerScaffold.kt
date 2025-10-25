package com.paam.songbook.ui.components

import Lyrics
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paam.songbook.model.Song
import com.paam.songbook.ui.fullplayer.FullPlayer
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility as BoxAnimatedVisibility

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

    if (currentSong == null) {
        content()
    } else {
        // ✅ Wrap scaffold in Box that respects nav bar insets
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.navigationBars.asPaddingValues())
        ) {
            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetPeekHeight = 72.dp,
                sheetDragHandle = {},
                sheetContent = {
                    Box(
                        modifier = Modifier.animateContentSize()
                    ) {
                        Crossfade(targetState = playerState, label = "PlayerCrossfade") { state ->
                            when (state) {
                                PlayerState.Mini -> {
                                    // ✅ Animated MiniPlayer with explicit modifier
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
                                            tonalElevation = 6.dp,
                                            shadowElevation = 8.dp,
                                            shape = MaterialTheme.shapes.medium,
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
            ) { content() }
        }
    }
}
