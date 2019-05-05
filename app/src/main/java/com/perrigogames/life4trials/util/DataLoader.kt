package com.perrigogames.life4trials.util

import android.content.Context
import android.os.Environment
import androidx.annotation.RawRes
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.nio.file.Files.exists
import java.nio.file.Files.delete
import android.media.MediaScannerConnection
import android.R
import android.content.res.Resources
import android.net.Uri
import android.util.Log


object DataUtil {

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Life4")
        storageDir.mkdirs()
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    @Throws(IOException::class)
    fun createExternalStoragePublicPicture(context: Context): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(path, timestamp)

        path.mkdirs()
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
        return file
    }

    fun deleteExternalStoragePublicPicture(name: String) {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        File(path, name).delete()
    }

    fun hasExternalStoragePublicPicture(name: String): Boolean {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File(path, name).exists()
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