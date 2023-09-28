package com.perrigogames.life4.api

import com.perrigogames.life4.api.base.*
import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.data.SongList
import com.perrigogames.life4.ktor.GithubDataAPI
import com.perrigogames.life4.util.indexOfOrEnd
import org.koin.core.KoinComponent
import org.koin.core.inject

class SongListRemoteData(
    reader: LocalDataReader,
    listener: NewDataListener<SongList>? = null,
): CompositeData<SongList>(listener), KoinComponent {

    private val githubKtor: GithubDataAPI by inject()

    private val converter = SongListConverter()

    override val rawData = LocalData(reader, converter)
    override val cacheData = CachedData(reader, converter, converter)
    override val remoteData = object: RemoteData<SongList>() {
        override suspend fun getRemoteResponse() = SongList.parse(githubKtor.getSongList())
    }

    private inner class SongListConverter: Converter<SongList> {
        override fun create(data: SongList) = data.toString()
        override fun create(s: String) = SongList.parse(s)
    }
}
