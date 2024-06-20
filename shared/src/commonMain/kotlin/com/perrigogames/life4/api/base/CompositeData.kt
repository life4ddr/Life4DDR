package com.perrigogames.life4.api.base

import co.touchlab.kermit.Logger
import com.perrigogames.life4.data.MajorVersioned
import com.perrigogames.life4.data.Versioned
import com.perrigogames.life4.injectLogger
import com.perrigogames.life4.model.BaseModel
import kotlinx.coroutines.flow.*

typealias VersionChange = Pair<Int, Int>

/**
 * A structure to unify the processes of reading raw data files, reading volatile cache files, and retrieving
 * remote files.
 */
abstract class CompositeData<T: Versioned>: BaseModel() {

    protected open val rawData: LocalData<T>? = null
    protected open val cacheData: CachedData<T>? = null
    protected open val remoteData: RemoteData<T>? = null

    protected open val logger: Logger by injectLogger(this::class.simpleName ?: "CompositeData")

    private val ready = MutableStateFlow(false)
    private val majorVersionBlocked = MutableStateFlow(false)

    private val _dataState: MutableStateFlow<LoadingState<T>> = MutableStateFlow(LoadingState.Loading())
    val dataState: StateFlow<LoadingState<T>> get() = combine(
        _dataState,
        ready,
    ) { dataState, ready ->
        if (!ready) {
            LoadingState.Loading()
        } else {
            dataState
        }
    }.stateIn(mainScope, started = SharingStarted.Eagerly, initialValue = LoadingState.Loading())

    private val currentData get() = (_dataState.value as? LoadingState.Loaded)?.data

    val versionState = combine(
        _dataState.unwrapLoaded().filterNotNull(),
        majorVersionBlocked,
    ) { data, majorVersionBlocked ->
        VersionInfo(
            version = data.version,
            majorVersion = (data as? MajorVersioned)?.majorVersion,
            versionString = if (data is MajorVersioned) {
                "${data.majorVersion}.${data.version}"
            } else {
                data.version.toString()
            },
            majorVersionBlocked = majorVersionBlocked,
        )
    }.stateIn(
        scope = mainScope,
        started = SharingStarted.Eagerly,
        initialValue = VersionInfo()
    )

    private val _versionChangeFlow = MutableSharedFlow<VersionChange>(replay = 1)
    val versionChangeFlow: SharedFlow<VersionChange> = _versionChangeFlow

    suspend fun start() {
        loadRawData()
        loadCachedData()
        ready.emit(true)
        loadRemoteData()
    }

    private suspend fun loadRawData() {
        rawData?.data?.let { _dataState.emit(LoadingState.Loaded(it)) }
    }

    private suspend fun loadCachedData() {
        cacheData?.data?.let { cache -> // load cache if it exists, delete the cache if it's behind the raw data
            if (shouldUpdateWith(cache)) {
                _dataState.emit(LoadingState.Loaded(cache))
            } else {
                _versionChangeFlow.emit(VersionChange(cache.version, currentData!!.version))
                cacheData!!.deleteCache()
//                listener?.onDataVersionChanged(currentData)
            }
        }
    }

    private fun loadRemoteData() {
        remoteData?.fetch(object: FetchListener<T> {
            override suspend fun onFetchUpdated(newData: T) {
                val currentMajorVersion = versionState.value.majorVersion
                if (
                    currentMajorVersion != null &&
                    (newData as MajorVersioned).majorVersion > currentMajorVersion
                ) { // new data has higher major version, do not use
                    majorVersionBlocked.emit(true)
                } else if (newData.version > (currentData?.version ?: 0)) { // new version is higher, use it and save it to cache
                    _versionChangeFlow.emit(VersionChange(currentData!!.version, newData.version))
                    _dataState.emit(LoadingState.Loaded(newData))
                    cacheData?.saveNewCache(newData)
                }
                // otherwise versions are the same
            }

            override suspend fun onFetchFailed(e: Throwable) {
                logger.e { e.message ?: "Undefined error" }
            }
        })
    }

    open fun shouldUpdateWith(newData: T): Boolean =
        newData.version > (currentData?.version ?: 0)

    sealed class LoadingState<T: Versioned> {
        class Loading<T: Versioned> : LoadingState<T>()
        data class Loaded<T: Versioned>(val data: T): LoadingState<T>()
    }
}

data class VersionInfo(
    val version: Int = 0,
    val majorVersion: Int? = null,
    val versionString: String = "0",
    val majorVersionBlocked: Boolean = false,
)

fun <T : Versioned> CompositeData.LoadingState<T>.unwrapLoaded(): T? {
    return (this as? CompositeData.LoadingState.Loaded<T>)?.data
}

fun <T : Versioned> Flow<CompositeData.LoadingState<T>>.unwrapLoaded(): Flow<T?> {
    return map { it.unwrapLoaded() }
}
