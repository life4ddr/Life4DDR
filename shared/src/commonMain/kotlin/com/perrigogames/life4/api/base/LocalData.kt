package com.perrigogames.life4.api.base

/**
 * Interface for retrieving data from the application's resources.
 */
interface LocalUncachedDataReader {
    /**
     * Loads a raw version of the data from the system's resources.
     * This is not optional and serves as the default data set.
     */
    fun loadInternalString(): String
}

/**
 * Interface for retrieving and committing data to/from a cached source,
 * usually elsewhere in local storage.
 */
interface LocalDataReader : LocalUncachedDataReader {
    /**
     * Loads the cached version of the data from internal storage, if it exists.
     */
    fun loadCachedString(): String?

    /**
     * Saves a set of data to the cache to be retrieved later.
     * @return whether the save was successful
     */
    fun saveCachedString(data: String): Boolean

    /**
     * Deletes the cached data, returning priority to the app's internal data.
     * @return whether the deletion was successful
     */
    fun deleteCachedString(): Boolean
}

class LocalData<T : Any>(
    private val localReader: LocalUncachedDataReader,
    private val stringToData: StringToData<T>,
) : InstantDataSource<T> {
    override val data: T
        get() = stringToData.create(localReader.loadInternalString())
}

class CachedData<T : Any>(
    private val localReader: LocalDataReader,
    private val dataToString: DataToString<T>,
    private val stringToData: StringToData<T>,
) : InstantDataSource<T> {
    override val data: T?
        get() =
            localReader.loadCachedString()
                ?.let { stringToData.create(it) }

    fun saveNewCache(data: T) = localReader.saveCachedString(dataToString.create(data))

    fun deleteCache() = localReader.deleteCachedString()
}
