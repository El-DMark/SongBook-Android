package com.paam.songbook.ui.fullplayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paam.songbook.media.formatTime

@Composable
fun SeekBarWithPreview(
    position: Long,
    duration: Long,
    sliderPosition: Float,
    onSliderChange: (Float) -> Unit,
    onSeekFinished: () -> Unit,
    previewTime: Long?,
    onPreviewTimeChange: (Long?) -> Unit,
    tint: Color
) {
    var isUserSeeking by remember { mutableStateOf(false) }
    var seekTarget by remember { mutableStateOf<Float?>(null) }

    // ✅ Decide what value to show
    val sliderValue = when {
        isUserSeeking -> sliderPosition
        seekTarget != null -> seekTarget!! // hold where user dropped
        duration > 0 -> position / duration.toFloat()
        else -> 0f
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = sliderValue,
            onValueChange = {
                isUserSeeking = true
                onSliderChange(it)
                onPreviewTimeChange((it * duration).toLong())
            },
            onValueChangeFinished = {
                seekTarget = sliderPosition // remember drop location
                onSeekFinished()
                isUserSeeking = false
                onPreviewTimeChange(null)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = tint,
                activeTrackColor = tint,
                inactiveTrackColor = tint.copy(alpha = 0.3f)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(position), style = MaterialTheme.typography.bodySmall, color = tint)
            Text(formatTime(duration), style = MaterialTheme.typography.bodySmall, color = tint)
        }
    }

    // ✅ Clear seekTarget once player catches up
    LaunchedEffect(position, duration) {
        if (seekTarget != null && duration > 0) {
            val playerValue = position / duration.toFloat()
            if (kotlin.math.abs(playerValue - seekTarget!!) < 0.01f) {
                seekTarget = null // player has caught up
            }
        }
    }
}