package com.perrigogames.life4.android.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

object DataUtil {
    //    val picturesDir: File
//        get() = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LIFE4").also {
//            it.mkdirs()
//        }

    val Context.picturesDir: File?
        get() = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    fun timestamp(
        locale: Locale,
        date: Date = Date(),
    ): String = SimpleDateFormat("yyyyMMdd_HHmmss", locale).format(date)

    @Throws(IOException::class)
    fun createTempFile(
        context: Context,
        locale: Locale,
    ): File = createTempFile(context, tempFilenameFromLocale(locale))

    @Throws(IOException::class)
    fun createTempFile(
        context: Context,
        filename: String,
    ): File = File.createTempFile(filename, ".jpg", context.picturesDir)

    fun tempFilenameFromLocale(locale: Locale) = "JPEG_${timestamp(locale)}_"

    fun resizeImage(
        outputFile: File,
        width: Int,
        height: Int,
        bitmap: Bitmap,
    ): String? {
        return try {
            val out = FileOutputStream(outputFile)
            scaleBitmap(bitmap, width, height).compress(Bitmap.CompressFormat.JPEG, 85, out)
            out.flush()
            out.close()
            outputFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun resizeImage(
        outputFile: FileDescriptor,
        width: Int,
        height: Int,
        bitmap: Bitmap,
    ): String? {
        return try {
            val out = FileOutputStream(outputFile)
            scaleBitmap(bitmap, width, height).compress(Bitmap.CompressFormat.JPEG, 85, out)
            out.flush()
            out.close()
            "FIXME"
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun scaleBitmap(
        bitmap: Bitmap,
        width: Int,
        height: Int,
    ): Bitmap {
        val scaleFactor = max(1, min(bitmap.width / width, bitmap.height / height)).toFloat()
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width / scaleFactor).toInt(),
            (bitmap.height / scaleFactor).toInt(),
            true,
        )
    }
}

@Suppress("DEPRECATION")
val Context.locale: Locale get() =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> resources.configuration.locales[0]
        else -> resources.configuration.locale
    }
