package com.perrigogames.life4.api

import com.perrigogames.life4.api.base.*
import com.perrigogames.life4.data.IgnoreListData
import com.perrigogames.life4.data.LadderRankData
import com.perrigogames.life4.data.LadderRankData.Companion.LADDER_RANK_MAJOR_VERSION
import com.perrigogames.life4.ktor.GithubDataAPI
import kotlinx.serialization.json.Json
import org.koin.core.KoinComponent
import org.koin.core.inject

class LadderRemoteData(
    reader: LocalDataReader,
    listener: NewDataListener<LadderRankData>? = null,
): CompositeData<LadderRankData>(listener), KoinComponent {

    private val json: Json by inject()
    private val githubKtor: GithubDataAPI by inject()

    private val converter = LadderRankDataConverter()

    override val rawData = LocalData(reader, converter)
    override val cacheData = CachedData(reader, converter, converter)
    override val remoteData = object: RemoteData<LadderRankData>() {
        override suspend fun getRemoteResponse() = githubKtor.getLadderRanks()
    }

    private inner class LadderRankDataConverter: Converter<LadderRankData> {
        override fun create(s: String) = json.decodeFromString(LadderRankData.serializer(), s)
        override fun create(data: LadderRankData) = json.encodeToString(LadderRankData.serializer(), data)
    }
}
