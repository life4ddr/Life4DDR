package com.perrigogames.life4trials.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RawRes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min


object DataUtil {

    val picturesDir: File
        get() = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LIFE4").also {
            it.mkdirs()
        }

    val gson: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(BaseRankGoal::class.java, "type")
                .registerSubtype(CaloriesRankGoal::class.java, CaloriesRankGoal.TYPE_STRING)
                .registerSubtype(SongSetClearGoal::class.java, SongSetClearGoal.TYPE_STRING)
                .registerSubtype(SongSetGoal::class.java, SongSetGoal.TYPE_STRING)
                .registerSubtype(DifficultyClearGoal::class.java, DifficultyClearGoal.TYPE_STRING)
                .registerSubtype(TrialGoal::class.java, TrialGoal.TYPE_STRING)
                .registerSubtype(MFCPointsGoal::class.java, MFCPointsGoal.TYPE_STRING)
                .registerSubtype(MultipleChoiceGoal::class.java, MultipleChoiceGoal.TYPE_STRING))
            .create()
    }

    fun timestamp(locale: Locale, date: Date = Date()): String = SimpleDateFormat("yyyyMMdd_HHmmss", locale).format(date)

    fun humanTimestamp(locale: Locale, date: Date = Date()): String = SimpleDateFormat("yyyy-MM-dd   hh:mm aa", locale).format(date)

    fun humanNewlineTimestamp(locale: Locale, date: Date = Date()): String = SimpleDateFormat("yyyy-MM-dd\nhh:mm aa", locale).format(date)

    @Throws(IOException::class)
    fun createTempFile(locale: Locale): File = createTempFile("JPEG_${timestamp(locale)}_")

    @Throws(IOException::class)
    fun createTempFile(filename: String): File = File.createTempFile(filename, ".jpg", picturesDir)

    fun deleteExternalStoragePublicPicture(name: String) {
        File(picturesDir, name).delete()
    }

    fun hasExternalStoragePublicPicture(name: String): Boolean = File(picturesDir, name).exists()

    fun createScaledBitmap(path: String, targetW: Int, targetH: Int): Bitmap? {
        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)

            // Determine how much to scale down the image
            val scaleFactor: Int = min(outWidth / targetW, outHeight / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            @Suppress("DEPRECATION")
            inPurgeable = true
        }
        return BitmapFactory.decodeFile(path, bmOptions)
    }

    fun resizeImage(locale: Locale, width: Int, height: Int, bitmap: Bitmap): String? =
        resizeImage(createTempFile(locale), width, height, bitmap)

    fun resizeImage(outputFile: File, width: Int, height: Int, bitmap: Bitmap): String? {
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

    fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val scaleFactor = max(1, min(bitmap.width / width, bitmap.height / height)).toFloat()
        return Bitmap.createScaledBitmap(bitmap,
            (bitmap.width / scaleFactor).toInt(),
            (bitmap.height / scaleFactor).toInt(),
            true)
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

fun Context.readFromFile(path: String): String? {
    var ret: String? = null
    try {
        InputStreamReader(openFileInput(path)).use { streamReader ->
            val bufferedReader = BufferedReader(streamReader)
            val builder = StringBuilder().also {
                var receiveString: String? = bufferedReader.readLine()
                while (receiveString != null) {
                    it.append(receiveString)
                    it.append('\n')
                    receiveString = bufferedReader.readLine()
                }
            }
            if (builder.isNotEmpty()) {
                ret = builder.toString()
            }
        }
    } catch (e: FileNotFoundException) {
        Log.e("DataUtil", "File not found: $e")
    } catch (e: IOException) {
        Log.e("DataUtil", "Can not read file: $e")
    }
    return ret
}

fun Context.saveToFile(path: String, content: String): Boolean {
    return try {
        OutputStreamWriter(openFileOutput(path, Context.MODE_PRIVATE)).use {
            it.write(content)
        }
        true
    } catch (e: Exception) { false }
}

fun ImageView.setScaledBitmapFromFile(path: String,
                                      targetW: Int = this.width,
                                      targetH: Int = this.height) {

    DataUtil.createScaledBitmap(path, targetW, targetH)?.also { bitmap ->
        setImageBitmap(bitmap)
    }
}

@Suppress("DEPRECATION")
val Context.locale: Locale get() = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> resources.configuration.locales[0]
    else -> resources.configuration.locale
}


fun List<String>.toListString(c: Context, lastModifierFormatRes: Int = R.string.or_s): String = StringBuilder().apply {
    this@toListString.forEachIndexed { index, d ->
        append(when {
            this@toListString.size == 1 -> d
            index == this@toListString.lastIndex -> c.getString(lastModifierFormatRes, d)
            index == this@toListString.lastIndex - 1 -> "$d "
            else -> "$d, "
        })
    }
}.toString()