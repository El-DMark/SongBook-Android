package com.paam.songbook.ui.fullplayer

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun extractDominantColorFromUrl(context: Context, imageUrl: String): Color? {
    return try {
        val loader = ImageLoader.Builder(context).build()
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .build()

        val result = loader.execute(request)
        if (result is SuccessResult) {
            val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
            bitmap?.let {
                val palette = Palette.from(it).generate()
                val rgb = palette.vibrantSwatch?.rgb ?: palette.dominantSwatch?.rgb
                rgb?.let { Color(it) }
            }
        } else null
    } catch (e: Exception) {
        null
    }
}

@Composable
fun rememberDominantColor(imageUrl: String?): Color? {
    val context = LocalContext.current
    var dominantColor by remember { mutableStateOf<Color?>(null) }

    LaunchedEffect(imageUrl) {
        if (!imageUrl.isNullOrEmpty()) {
            dominantColor = extractDominantColorFromUrl(context, imageUrl)
        }
    }

    return dominantColor
}

