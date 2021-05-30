package com.perrigogames.life4.api

import com.perrigogames.life4.data.LadderRankData
import com.perrigogames.life4.data.LadderRankData.Companion.LADDER_RANK_MAJOR_VERSION
import com.perrigogames.life4.ktor.GithubDataAPI
import org.koin.core.inject

class LadderRemoteData(reader: LocalDataReader,
                       fetchListener: FetchListener<LadderRankData>? = null):
    KtorMajorVersionedRemoteData<LadderRankData>(reader, LADDER_RANK_MAJOR_VERSION, fetchListener) {

    private val githubKtor: GithubDataAPI by inject()

    override fun createLocalDataFromText(text: String): LadderRankData {
        return json.decodeFromString(LadderRankData.serializer(), text)
    }

    override suspend fun getRemoteResponse() = githubKtor.getLadderRanks()
    override fun createTextToData(data: LadderRankData) = json.encodeToString(LadderRankData.serializer(), data)
}
