package com.paam.songbook.ui.fullplayer

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerHeaderBar(
    onCollapse: () -> Unit,
    showLyrics: Boolean,
    matchedLyricsAvailable: Boolean,
    onToggleLyrics: () -> Unit,
    onShare: () -> Unit = {},
    sleepTimerEnd: Long? = null,
    stopAfterCurrentTrack: Boolean = false,
    onSetSleepTimer: (Int) -> Unit = {},
    onSetStopAfterCurrentTrack: (Boolean) -> Unit = {},
    tint: Color // ✅ new
) {
    var showTimerSheet by remember { mutableStateOf(false) }
    var sliderMinutes by remember { mutableFloatStateOf(15f) }
    var remainingSeconds by remember { mutableStateOf<Long?>(null) }

    // 🔹 Live countdown effect
    LaunchedEffect(sleepTimerEnd) {
        if (sleepTimerEnd != null) {
            while (true) {
                val remaining = sleepTimerEnd - System.currentTimeMillis()
                if (remaining > 0) {
                    remainingSeconds = remaining / 1000
                } else {
                    remainingSeconds = null
                    break
                }
                delay(1000)
            }
        } else {
            remainingSeconds = null
        }
    }

    if (showTimerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTimerSheet = false },
            containerColor = Color(0xFF121212).copy(alpha = 0.95f), // Darker, sleeker background
            contentColor = Color.White
        ) {
            Box(modifier = Modifier.fillMaxWidth().background(
                Brush.verticalGradient(listOf(Color(0xFF2C1E4A).copy(alpha=0.3f), Color.Transparent))
            )) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Bedtime, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFFA388EE))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Sleep Timer", style = androidx.compose.material3.MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (sleepTimerEnd != null || stopAfterCurrentTrack) {
                        Text(
                            text = if (stopAfterCurrentTrack) "Stopping after current track" else "Active Timer: ${remainingSeconds?.let { String.format("%02d:%02d", it / 60, it % 60) }}",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { onSetSleepTimer(0); onSetStopAfterCurrentTrack(false); showTimerSheet = false },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336))
                        ) {
                            Text("Turn Off Timer")
                        }
                    } else {
                        Text("Stop playback after:", color = Color.LightGray)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text("${sliderMinutes.toInt()} minutes", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        
                        Slider(
                            value = sliderMinutes,
                            onValueChange = { sliderMinutes = it },
                            valueRange = 1f..120f,
                            steps = 119,
                            modifier = Modifier.fillMaxWidth(0.9f),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFA388EE),
                                activeTrackColor = Color(0xFFA388EE)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onSetSleepTimer(sliderMinutes.toInt()); showTimerSheet = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA388EE))
                        ) {
                            Text("Start Timer", color = Color.White)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("OR", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedButton(onClick = { onSetStopAfterCurrentTrack(true); showTimerSheet = false }) { 
                            Text("Stop after current track", color = Color.White) 
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp)
    ) {
        IconButton(
            onClick = onCollapse,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ExpandMore, contentDescription = "Collapse", tint = tint)
        }

        Row(modifier = Modifier.align(Alignment.TopEnd)) {
            IconButton(
                onClick = onShare,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = tint)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { showTimerSheet = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    val timerActive = sleepTimerEnd != null || stopAfterCurrentTrack
                    Icon(
                        if (timerActive) Icons.Default.Bedtime else Icons.Default.Timer, 
                        contentDescription = "Sleep Timer", 
                        tint = if (timerActive) Color(0xFFA388EE) else tint
                    )
                }
                if (sleepTimerEnd != null && remainingSeconds != null) {
                    Text(
                        text = String.format("%02d:%02d", remainingSeconds!! / 60, remainingSeconds!! % 60),
                        color = Color(0xFFA388EE),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (stopAfterCurrentTrack) {
                    Text("Track", color = Color(0xFFA388EE), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (matchedLyricsAvailable) {
                IconButton(
                    onClick = onToggleLyrics,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (showLyrics) Icons.Default.Close else Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = if (showLyrics) "Hide Lyrics" else "Show Lyrics",
                        tint = tint
                    )
                }
            }
        }
    }
}


