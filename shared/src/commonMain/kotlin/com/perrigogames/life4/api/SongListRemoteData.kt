package com.perrigogames.life4.api

import co.touchlab.kermit.Logger
import com.perrigogames.life4.api.base.*
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.ktor.SanbaiAPI
import com.perrigogames.life4.ktor.SongListResponse
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class SongListRemoteData: CompositeData<SongListResponse>(), KoinComponent {

    private val json: Json by inject()
    private val reader: LocalDataReader by inject(named(SONGS_FILE_NAME))
    private val sanbaiApi: SanbaiAPI by inject()

    private val converter = SongListConverter()

    override val rawData = LocalData(reader, converter)
    override val cacheData = CachedData(reader, converter, converter)
    override val remoteData = object: RemoteData<SongListResponse>() {
        override val logger: Logger = this@SongListRemoteData.logger
        override suspend fun getRemoteResponse() = sanbaiApi.getSongData()
    }

    private inner class SongListConverter: Converter<SongListResponse> {
        override fun create(s: String): SongListResponse =
            json.decodeFromString(SongListResponse.serializer(), s)

        override fun create(data: SongListResponse) =
            json.encodeToString(SongListResponse.serializer(), data)
    }
}
