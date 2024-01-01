package com.perrigogames.life4.api.base

import com.perrigogames.life4.data.MajorVersioned
import com.perrigogames.life4.data.Versioned
import com.perrigogames.life4.logE
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A structure to unify the processes of reading raw data files, reading volatile cache files, and retrieving
 * remote files.
 */
abstract class CompositeData<T: Versioned>(
    private val listener: NewDataListener<T>?,
): BaseModel() {

    protected open val rawData: LocalData<T>? = null
    protected open val cacheData: CachedData<T>? = null
    protected open val remoteData: RemoteData<T>? = null

    @Deprecated("Use dataFlow instead")
    val data: T get() = _dataFlow.value

    private lateinit var _dataFlow: MutableStateFlow<T>
    val dataFlow: StateFlow<T> get() = _dataFlow
    private val currentData get() = _dataFlow.value

    val versionString: String
        get() = (currentData as? MajorVersioned)?.let {
            "${it.majorVersion}.${it.version}"
        } ?: currentData.version.toString()

    private val majorVersion: Int?
        get() = (currentData as? MajorVersioned)?.majorVersion

    fun start() {
        loadRawData()
        loadCachedData()
        listener?.onDataLoaded(currentData)
        loadRemoteData()
    }

    private fun loadRawData() {
        if (rawData != null) { // load raw data first
            _dataFlow.tryEmit(currentData)
        }
    }

    private fun loadCachedData() {
        if (cacheData != null) { // load cache if it exists, delete the cache if it's behind the raw data
            val cache = cacheData!!.data
            if (cache != null && shouldUpdate(cache)) {
                _dataFlow.tryEmit(cache)
            } else {
                cacheData!!.deleteCache()
                listener?.onDataVersionChanged(currentData)
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
                } else if (newData.version > currentData.version) { // new version is higher, use it and save it to cache
                    _dataFlow.tryEmit(newData)
                    listener?.onDataLoaded(currentData)
                    cacheData?.saveNewCache(currentData)
                    listener?.onDataVersionChanged(currentData)
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
        newData.version > currentData.version
}
