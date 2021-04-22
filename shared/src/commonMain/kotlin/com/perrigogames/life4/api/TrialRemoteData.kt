package com.perrigogames.life4.api

import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.data.TrialData.Companion.TRIAL_DATA_REMOTE_VERSION
import com.perrigogames.life4.isDebug
import com.perrigogames.life4.ktor.GithubDataAPI
import org.koin.core.inject

class TrialRemoteData(reader: LocalDataReader,
                      fetchListener: FetchListener<TrialData>? = null):
    KtorMajorVersionedRemoteData<TrialData>(reader, TRIAL_DATA_REMOTE_VERSION, fetchListener) {

    private val githubKtor: GithubDataAPI by inject()

    override fun createLocalDataFromText(text: String): TrialData {
        val data = json.decodeFromString(TrialData.serializer(), text)
        validateTrialData(data)
        return mergeDebugData(data)
    }

    override suspend fun getRemoteResponse() = githubKtor.getTrials()
    override fun createTextToData(data: TrialData) = json.encodeToString(TrialData.serializer(), data)

    override fun onFetchUpdated(data: TrialData) {
        super.onFetchUpdated(data)
        validateTrialData(data)
        this.data = mergeDebugData(data)
    }

    private fun mergeDebugData(data: TrialData): TrialData = if (isDebug) {
        //FIXME debug data
//        val debugData: TrialData = json.parse(TrialData.serializer(), (localReader.loadRawString()))
//        val placements = placementManager.placements
//        TrialData(
//            data.version,
//            data.majorVersion,
//            data.trials + debugData.trials + placements
//        )
        data
    } else data

    private fun validateTrialData(data: TrialData) {
        data.trials.firstOrNull { !it.isExValid }?.let { trial -> throw Exception(
            "Trial ${trial.name} (${trial.total_ex}) has improper EX scores: ${trial.songs.map { it.ex }.joinToString()}") }
    }
}
