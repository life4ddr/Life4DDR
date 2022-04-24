package com.perrigogames.life4.android

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.api.base.LocalUncachedDataReader
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.*

/**
 * Android implementations of [LocalUncachedDataReader] and [LocalDataReader]
 */

open class AndroidUncachedDataReader(@RawRes protected val rawResId: Int):
    LocalUncachedDataReader, KoinComponent {

    protected val context: Context by inject()
    override fun loadInternalString(): String = context.loadRawString(rawResId)
}

class AndroidDataReader(rawResId: Int, private val cachedFileName: String):
    AndroidUncachedDataReader(rawResId), LocalDataReader {

    override fun loadCachedString(): String? = context.readFromFile(cachedFileName)

    override fun saveCachedString(data: String) = context.saveToFile(cachedFileName, data)

    override fun deleteCachedString() = context.deleteFile(cachedFileName)
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
