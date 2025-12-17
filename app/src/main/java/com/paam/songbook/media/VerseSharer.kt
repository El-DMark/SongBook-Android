package com.paam.songbook.media

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max


object VerseSharer {

    fun shareVerseAsImage(
        context: Context,
        verseData: VerseData,
        imageUrl: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            // Load background image
            val bg = try {
                val req = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .allowHardware(false)
                    .build()
                context.imageLoader.execute(req).drawable?.toBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } ?: return@launch

            // Draw verse text onto the bitmap
            val finalBitmap = drawVerseToBitmap(bg, verseData)

            // Save to cache
            val imageUri = try {
                val cachePath = File(context.cacheDir, "images").apply { mkdirs() }
                // Use a unique file name to prevent caching issues with the share sheet
                val file = File(cachePath, "verse_to_share_${System.currentTimeMillis()}.png")
                FileOutputStream(file).use {
                    finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } ?: return@launch

            // --- THIS IS THE FIX: More robust sharing logic ---
            withContext(Dispatchers.Main) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    // Add the URI to the clip data for robust sharing
                    clipData = ClipData.newUri(context.contentResolver, "Verse Image", imageUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(shareIntent, "Share Verse Image")

                // Manually grant permissions to every app that can handle the intent
                // This is the key to making the preview image appear reliably.
                val resInfoList = context.packageManager.queryIntentActivities(chooser, 0)
                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    context.grantUriPermission(
                        packageName,
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                context.startActivity(chooser)
            }
            // --- END OF FIX ---
        }
    }

    private fun drawVerseToBitmap(background: Bitmap, verseData: VerseData): Bitmap {
        val outW = 1080
        val outH = 1080
        val out = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)

        // Center-crop background
        val scale = max(outW / background.width.toFloat(), outH / background.height.toFloat())
        val scaledW = background.width * scale
        val scaledH = background.height * scale
        val dx = (outW - scaledW) / 2f
        val dy = (outH - scaledH) / 2f
        val dst = RectF(dx, dy, dx + scaledW, dy + scaledH)
        canvas.drawBitmap(background, null, dst, null)

        // Dark scrim for readability
        val scrimPaint = Paint().apply { color = Color.argb(120, 0, 0, 0) }
        canvas.drawRect(0f, 0f, outW.toFloat(), outH.toFloat(), scrimPaint)

        // Text paints
        val versePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 54f
        }
        val refPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(230, 255, 255, 255)
            textSize = 42f
        }
        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 255, 255, 255)
            textSize = 34f
        }

        val sidePadding = 96
        val maxTextWidth = outW - sidePadding * 2

        // Layouts
        val quoteText = "\"${verseData.text}\""
        val verseLayout = StaticLayout.Builder
            .obtain(quoteText, 0, quoteText.length, versePaint, maxTextWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()

        val refText = "- ${verseData.reference}"
        val refLayout = StaticLayout.Builder
            .obtain(refText, 0, refText.length, refPaint, maxTextWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()

        val footerText = "Shared from Tehillah\nGod Bless You"
        val footerLayout = StaticLayout.Builder
            .obtain(footerText, 0, footerText.length, footerPaint, maxTextWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()

        // Position vertically
        val totalHeight = verseLayout.height + 24 + refLayout.height + 48 + footerLayout.height
        val startY = (outH - totalHeight) / 2f

        canvas.save()
        canvas.translate(sidePadding.toFloat(), startY)
        verseLayout.draw(canvas)
        canvas.translate(0f, verseLayout.height + 24f)
        refLayout.draw(canvas)
        canvas.translate(0f, refLayout.height + 48f)
        footerLayout.draw(canvas)
        canvas.restore()

        return out
    }
}
