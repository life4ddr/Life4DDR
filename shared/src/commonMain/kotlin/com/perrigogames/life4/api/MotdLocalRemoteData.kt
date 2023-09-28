package com.perrigogames.life4.api

import com.perrigogames.life4.api.base.*
import com.perrigogames.life4.data.LadderRankData
import com.perrigogames.life4.data.MessageOfTheDay
import com.perrigogames.life4.ktor.GithubDataAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.KoinComponent
import org.koin.core.inject

class MotdLocalRemoteData(
    reader: LocalDataReader,
    listener: NewDataListener<MessageOfTheDay>? = null,
): CompositeData<MessageOfTheDay>(listener), KoinComponent {

    private val json: Json by inject()
    private val githubKtor: GithubDataAPI by inject()

    private val converter = MessageOfTheDayConverter()

    override val rawData = LocalData(reader, converter)
    override val cacheData = CachedData(reader, converter, converter)
    override val remoteData = object: RemoteData<MessageOfTheDay>() {
        override suspend fun getRemoteResponse() = githubKtor.getMotd()
    }

    private inner class MessageOfTheDayConverter: Converter<MessageOfTheDay> {
        override fun create(s: String) = json.decodeFromString(MessageOfTheDay.serializer(), s)
        override fun create(data: MessageOfTheDay) = json.encodeToString(MessageOfTheDay.serializer(), data)
    }
}