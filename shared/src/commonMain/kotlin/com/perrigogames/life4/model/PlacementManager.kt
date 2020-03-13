package com.perrigogames.life4.model

import com.perrigogames.life4.api.LocalUncachedDataReader
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4.isDebug
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.PLACEMENTS_FILE_NAME
import kotlinx.serialization.json.Json
import org.koin.core.inject
import org.koin.core.qualifier.named

class PlacementManager: BaseModel() {

    private val json: Json by inject()
    private val dataReader: LocalUncachedDataReader by inject(named(PLACEMENTS_FILE_NAME))

    private val placementData: TrialData = json.parse(TrialData.serializer(), dataReader.loadRawString())

    val placements get() = placementData.trials

    fun findPlacement(id: String) = placements.firstOrNull { it.id == id }

    fun previousPlacement(id: String) = previousPlacement(placements.indexOfFirst { it.id == id })

    fun previousPlacement(index: Int) = placements.getOrNull(index - 1)

    fun nextPlacement(id: String) = nextPlacement(placements.indexOfFirst { it.id == id })

    fun nextPlacement(index: Int) = placements.getOrNull(index + 1)

    override fun onApplicationException() {
        if (!isDebug) {
            //FIXME Crashlytics
//            Crashlytics.setString("placements", placements.joinToString { it.id })
        }
    }
}
