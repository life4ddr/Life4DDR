package com.perrigogames.life4.api

import com.perrigogames.life4.api.base.CachedData
import com.perrigogames.life4.api.base.CompositeData
import com.perrigogames.life4.api.base.Converter
import com.perrigogames.life4.api.base.LocalData
import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.feature.partialdifficulty.PartialDifficultyResponse
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PartialDifficultyRemoteData(
    reader: LocalDataReader,
): CompositeData<PartialDifficultyResponse>(), KoinComponent {

    private val json: Json by inject()
//    private val sabnaiKtor: SanbaiAPI by inject()

    private val converter = PartialDifficultyResponseConverter()

    override val rawData = LocalData(reader, converter)
    override val cacheData = CachedData(reader, converter, converter)
    // TODO
//    override val remoteData = object: RemoteData<MessageOfTheDay>() {
//        override suspend fun getRemoteResponse() = sabnaiKtor.getMotd()
//    }

    private inner class PartialDifficultyResponseConverter: Converter<PartialDifficultyResponse> {
        override fun create(s: String) = json.decodeFromString(PartialDifficultyResponse.serializer(), s)
        override fun create(data: PartialDifficultyResponse) = json.encodeToString(PartialDifficultyResponse.serializer(), data)
    }
}