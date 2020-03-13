package com.perrigogames.life4trials.api

import android.content.Context
import androidx.annotation.RawRes
import com.perrigogames.life4.api.LocalDataReader
import com.perrigogames.life4.api.LocalUncachedDataReader
import com.perrigogames.life4trials.util.loadRawString
import com.perrigogames.life4trials.util.readFromFile
import com.perrigogames.life4trials.util.saveToFile
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Android implementations of [LocalUncachedDataReader] and [LocalDataReader]
 */

open class AndroidUncachedDataReader(@RawRes protected val rawResId: Int):
    LocalUncachedDataReader, KoinComponent {

    protected val context: Context by inject()
    override fun loadRawString(): String = context.loadRawString(rawResId)
}

class AndroidDataReader(rawResId: Int, private val cachedFileName: String):
    AndroidUncachedDataReader(rawResId), LocalDataReader {

    override fun loadCachedString(): String? = context.readFromFile(cachedFileName)

    override fun saveCachedString(data: String) = context.saveToFile(cachedFileName, data)

    override fun deleteCachedString() = context.deleteFile(cachedFileName)
}
