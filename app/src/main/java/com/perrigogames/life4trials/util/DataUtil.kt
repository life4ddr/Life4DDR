package com.perrigogames.life4trials.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.annotation.RawRes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object DataUtil {

    val gson: Gson by lazy {
        GsonBuilder().create()
    }

    @Throws(IOException::class)
    fun createImageFile(locale: Locale): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", locale).format(Date())
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LIFE4")
        storageDir.mkdirs()
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    fun deleteExternalStoragePublicPicture(name: String) {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        File(path, name).delete()
    }

    fun hasExternalStoragePublicPicture(name: String): Boolean {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File(path, name).exists()
    }

    fun createScaledBitmap(path: String, targetW: Int, targetH: Int): Bitmap? {
        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            @Suppress("DEPRECATION")
            inPurgeable = true
        }
        return BitmapFactory.decodeFile(path, bmOptions)
    }

    fun scaleSavedImage(path: String, targetW: Int, targetH: Int, contentResolver: ContentResolver): Boolean {
        try {
            val file = File(path)
            val pictureBitmap = createScaledBitmap(path, targetW, targetH) ?: return false
            FileOutputStream(file).use {
                pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
                it.flush()
                it.close()
            }

            MediaStore.Images.Media.insertImage(contentResolver, file.absolutePath, file.name, file.name)
        } catch (e: Exception) {
            return false
        }
        return true
    }
}

fun Context.loadRawString(@RawRes res: Int): String {
    val writer = StringWriter()
    resources.openRawResource(res).use { input ->
        val reader = BufferedReader(InputStreamReader(input, "UTF-8"))
        val buffer = CharArray(1024)
        var n: Int = reader.read(buffer)
        while (n != -1) {
            writer.write(buffer, 0, n)
            n = reader.read(buffer)
        }
    }
    return writer.toString()
}

fun ImageView.setScaledBitmapFromFile(path: String,
                                      targetW: Int = this.width,
                                      targetH: Int = this.height) {

    DataUtil.createScaledBitmap(path, targetW, targetH)?.also { bitmap ->
        this.setImageBitmap(bitmap)
    }
}