package com.perrigogames.life4.api.base

import com.perrigogames.life4.data.MajorVersioned
import com.perrigogames.life4.data.Versioned
import com.perrigogames.life4.logE

/**
 * A structure to unify the processes of reading raw data files, reading volatile cache files, and retrieving
 * remote files.
 */
abstract class CompositeData<T: Versioned>(
    private val listener: NewDataListener<T>?,
) {

    open val rawData: LocalData<T>? = null
    open val cacheData: CachedData<T>? = null
    open val remoteData: RemoteData<T>? = null

    lateinit var data: T

    val versionString: String
        get() = (data as? MajorVersioned)?.let {
            "${it.majorVersion}.${it.version}"
        } ?: data.version.toString()

    private var majorVersion: Int? = null

    fun start() {
        loadRawData()
        loadCachedData()
        listener?.onDataLoaded(data)
        loadRemoteData()
    }

    private fun loadRawData() {
        if (rawData != null) { // load raw data first
            data = rawData!!.data
            (data as? MajorVersioned)?.let {
                majorVersion = it.majorVersion
            }
        }
    }

    private fun loadCachedData() {
        if (cacheData != null) { // load cache if it exists, delete the cache if it's behind the raw data
            val cache = cacheData!!.data
            if (cache != null && shouldUpdate(cache)) {
                data = cache
            } else {
                cacheData!!.deleteCache()
                listener?.onDataVersionChanged(data)
            }
        }
    }

    private fun loadRemoteData() {
        remoteData?.fetch(object: FetchListener<T> {
            override fun onFetchUpdated(newData: T) {
                if (
                    majorVersion != null &&
                    (newData as MajorVersioned).majorVersion > majorVersion!!
                ) { // new data has higher major version, do not use
                    listener?.onMajorVersionBlock()
                } else if (newData.version > data.version) { // new version is higher, use it and save it to cache
                    data = newData
                    listener?.onDataLoaded(data)
                    cacheData?.saveNewCache(data)
                    listener?.onDataVersionChanged(data)
                }
                // otherwise versions are the same
            }

            override fun onFetchFailed(e: Throwable) {
                logE("CompositeData", e.message ?: "Undefined error")
            }
        })
    }

    interface NewDataListener<T: Versioned> {
        /**
         * Invoked the first time data is loaded and any time the currently loaded data is changed
         */
        fun onDataLoaded(data: T) {}

        /**
         * Invoked when a new data set has a compatible minor version update and has been applied internally
         */
        fun onDataVersionChanged(data: T) {}

        /**
         * Invoked when a new data set has an incompatible major version and cannot be loaded
         */
        fun onMajorVersionBlock() {}
    }

    open fun shouldUpdate(newData: T): Boolean =
        newData.version > data.version
}
