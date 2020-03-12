package com.perrigogames.life4trials.api

import android.content.Context
import androidx.annotation.RawRes
import com.perrigogames.life4trials.util.loadRawString
import org.koin.core.KoinComponent
import org.koin.core.inject

class AndroidDataReader(@RawRes private val rawResId: Int,
                        private val cachedFilename: String): LocalDataReader, KoinComponent {

    private val context: Context by inject()

    override fun loadRawString(): String = context.loadRawString(rawResId)

    override fun loadCachedString(): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveCachedString(data: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteCachedString() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
