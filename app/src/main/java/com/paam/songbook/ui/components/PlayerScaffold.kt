package com.paam.songbook.ui.components

import Lyrics
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paam.songbook.model.Song
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
    // 🔹 Added parameters to support Favorites implementation
    isFavorite: Boolean = false,
    onToggleFavorite: (Boolean) -> Unit = {},
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    shuffleModeEnabled: Boolean = false,
    repeatMode: Int = 0,
    onToggleShuffle: () -> Unit = {},
    onToggleRepeat: () -> Unit = {},
    sleepTimerEnd: Long? = null,
    stopAfterCurrentTrack: Boolean = false,
    onSetSleepTimer: (Int) -> Unit = {},
    onSetStopAfterCurrentTrack: (Boolean) -> Unit = {},
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
        //  .padding(WindowInsets.navigationBars.asPaddingValues())
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = if (currentSong == null) 0.dp else 116.dp, // collapse when no song
            sheetDragHandle = {},
            sheetContainerColor = Color.Transparent,   // no background behind mini player
            sheetContent = {
                if (currentSong != null) {
                    val isExpanded = scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded
                    
                    val fullPlayerAlpha by animateFloatAsState(
                        targetValue = if (isExpanded) 1f else 0f,
                        animationSpec = tween(300, easing = LinearOutSlowInEasing),
                        label = "fullAlpha"
                    )
                    
                    val miniPlayerAlpha by animateFloatAsState(
                        targetValue = if (isExpanded) 0f else 1f,
                        animationSpec = tween(200, easing = FastOutLinearInEasing),
                        label = "miniAlpha"
                    )

                    Box(modifier = Modifier.fillMaxSize()) {
                        // Full Player Layer
                        if (fullPlayerAlpha > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(fullPlayerAlpha)
                            ) {
                                FullPlayer(
                                    currentSong = currentSong,
                                    isPlaying = isPlaying,
                                    position = position,
                                    duration = duration,
                                    isFavorite = isFavorite,
                                    onToggleFavorite = onToggleFavorite,
                                    onCollapse = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } },
                                    onPlayPause = onPlayPause,
                                    onNext = onNext,
                                    onPrevious = onPrevious,
                                    onSeek = onSeek,
                                    shuffleModeEnabled = shuffleModeEnabled,
                                    repeatMode = repeatMode,
                                    onToggleShuffle = onToggleShuffle,
                                    onToggleRepeat = onToggleRepeat,
                                    sleepTimerEnd = sleepTimerEnd,
                                    stopAfterCurrentTrack = stopAfterCurrentTrack,
                                    onSetSleepTimer = onSetSleepTimer,
                                    onSetStopAfterCurrentTrack = onSetStopAfterCurrentTrack,
                                    lyrics = lyrics
                                )
                            }
                        }

                        // Mini Player Layer
                        if (miniPlayerAlpha > 0f) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .alpha(miniPlayerAlpha)
                            ) {
                                Surface(
                                    tonalElevation = 0.dp,
                                    shadowElevation = 0.dp,
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(16.dp),
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
                    }
                }
            }
        ) { content() }
    }
}