package com.yakshagana.loka.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.FileProvider
import com.yakshagana.loka.model.Event
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

object PosterShare {
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

    fun shareEventPoster(context: Context, event: Event) {
        val bitmap = buildPoster(event)
        val file = File(context.cacheDir, "${event.id}_poster.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Join ${event.title} by ${event.melaName} at ${event.venue}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Yakshagana Poster"))
    }

    private fun buildPoster(event: Event): Bitmap {
        val width = 1080
        val height = 1440
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.parseColor("#1A1A1A"))

        val panel = Paint().apply { color = Color.parseColor("#B71C1C") }
        canvas.drawRoundRect(RectF(60f, 60f, width - 60f, height - 60f), 40f, 40f, panel)

        val goldLine = Paint().apply {
            color = Color.parseColor("#C9A227")
            strokeWidth = 8f
        }
        canvas.drawLine(120f, 280f, width - 120f, 280f, goldLine)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 68f
            isFakeBoldText = true
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFF8E7")
            textSize = 44f
        }

        canvas.drawText("YAKSHAGANA-LOKA", 130f, 210f, titlePaint)
        canvas.drawText(event.title, 130f, 390f, titlePaint)
        canvas.drawText("Mela: ${event.melaName}", 130f, 510f, bodyPaint)
        canvas.drawText("Venue: ${event.venue}", 130f, 590f, bodyPaint)
        canvas.drawText("Time: ${event.dateTime.format(formatter)}", 130f, 670f, bodyPaint)
        canvas.drawText("Celebrate Coastal Heritage", 130f, 810f, bodyPaint)

        return bitmap
    }
}
