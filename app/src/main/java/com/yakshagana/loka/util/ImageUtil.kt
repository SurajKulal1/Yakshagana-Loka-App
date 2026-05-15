package com.yakshagana.loka.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.yakshagana.loka.R
import java.io.File
import java.io.InputStream

fun copyToPrivateStorage(context: Context, sourceUri: Uri?): Uri? {
    return try {
        if (sourceUri == null) return null
        val inputStream: InputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
        val fileName = "event_thumb_${System.currentTimeMillis()}.jpg"
        val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        Uri.fromFile(context.getFileStreamPath(fileName))
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getThumbnailModel(context: Context, thumbnailUri: String?): Any {
    return try {
        when {
            thumbnailUri == null -> R.drawable.yakshagana_slide_1
            thumbnailUri.startsWith("content://") -> thumbnailUri
            thumbnailUri.startsWith("file://") -> {
                val file = File(Uri.parse(thumbnailUri).path ?: return R.drawable.yakshagana_slide_1)
                if (file.exists()) {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                } else {
                    R.drawable.yakshagana_slide_1
                }
            }
            thumbnailUri.startsWith("http://") || thumbnailUri.startsWith("https://") -> thumbnailUri
            thumbnailUri == "yakshagana_slide_1" -> R.drawable.yakshagana_slide_1
            thumbnailUri == "yakshagana_slide_2" -> R.drawable.yakshagana_slide_2
            thumbnailUri == "yakshagana_slide_3" -> R.drawable.yakshagana_slide_3
            thumbnailUri == "yakshagana_slide_4" -> R.drawable.yakshagana_slide_4
            thumbnailUri == "yakshagana_slide_5" -> R.drawable.yakshagana_slide_5
            thumbnailUri == "yakshagana_slide_6" -> R.drawable.yakshagana_slide_6
            thumbnailUri == "yakshagana_slide_7" -> R.drawable.yakshagana_slide_7
            else -> R.drawable.yakshagana_slide_1
        }
    } catch (e: Exception) {
        e.printStackTrace()
        R.drawable.yakshagana_slide_1
    }
}
