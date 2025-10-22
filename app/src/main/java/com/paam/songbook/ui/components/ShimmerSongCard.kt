package com.paam.songbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerSongCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(160.dp)
            .padding(4.dp)
    ) {
        // Album art placeholder
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .shimmerEffect() // 👈 use your custom shimmer
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Title placeholder
        Box(
            modifier = Modifier
                .height(16.dp)
                .fillMaxWidth(0.8f)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Artist placeholder
        Box(
            modifier = Modifier
                .height(14.dp)
                .fillMaxWidth(0.6f)
                .shimmerEffect()
        )
    }
}
