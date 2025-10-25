package com.paam.songbook.ui.fullplayer

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.paam.songbook.ui.formatTime

@Composable
fun SeekBarWithPreview(
    position: Long,
    duration: Long,
    sliderPosition: Float,
    onSliderChange: (Float) -> Unit,
    onSeekFinished: () -> Unit,
    previewTime: Long?,
    onPreviewTimeChange: (Long?) -> Unit,
    tint: Color // ✅ new
) {
    val density = LocalDensity.current
    val sliderValue = if (duration > 0) position / duration.toFloat() else 0f
    val animatedSliderValue by animateFloatAsState(
        targetValue = sliderPosition.takeIf { it >= 0f } ?: sliderValue,
        animationSpec = tween(300),
        label = "SliderAnim"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxWidthDp = maxWidth

        Box(modifier = Modifier.fillMaxWidth()) {
            Crossfade(targetState = previewTime, label = "PreviewBubble") { time: Long? ->
                if (time != null && duration > 0) {
                    val offsetPercent = animatedSliderValue.coerceIn(0f, 1f)
                    val rawOffset = offsetPercent * (maxWidthPx - 32)
                    val offsetDp = with(density) { rawOffset.toDp() }
                    val clampedOffset = offsetDp.coerceIn(0.dp, maxWidthDp - 40.dp)

                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = clampedOffset)
                                .width(2.dp)
                                .height(24.dp)
                                .background(tint)
                        )
                        Text(
                            text = formatTime(time),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White, // keep bubble text white
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = clampedOffset - 20.dp, y = (-28).dp)
                                .background(tint, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Slider(
                value = animatedSliderValue,
                onValueChange = {
                    onSliderChange(it)
                    onPreviewTimeChange((it * duration).toLong())
                },
                onValueChangeFinished = {
                    onSeekFinished()
                    onPreviewTimeChange(null)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = tint,
                    activeTrackColor = tint,
                    inactiveTrackColor = tint.copy(alpha = 0.3f)
                )
            )
        }
    }

    //Spacer(Modifier.height(2.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(formatTime(position), style = MaterialTheme.typography.bodySmall, color = tint)
        Text(formatTime(duration), style = MaterialTheme.typography.bodySmall, color = tint)
    }
}
