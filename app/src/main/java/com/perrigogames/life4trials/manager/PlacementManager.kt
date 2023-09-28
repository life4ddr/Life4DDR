package com.perrigogames.life4trials.manager

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialData
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString

class PlacementManager(context: Context): BaseManager() {

    private val placementData: TrialData =
        DataUtil.gson.fromJson(context.loadRawString(R.raw.placements), TrialData::class.java)!!

    val placements get() = placementData.trials

    fun findPlacement(id: String) = placements.firstOrNull { it.id == id }

    fun previousPlacement(id: String) = previousPlacement(placements.indexOfFirst { it.id == id })

    fun previousPlacement(index: Int) = placements.getOrNull(index - 1)

    fun nextPlacement(id: String) = nextPlacement(placements.indexOfFirst { it.id == id })

    fun nextPlacement(index: Int) = placements.getOrNull(index + 1)

    override fun onApplicationException() {
        Crashlytics.setString("placements", placements.joinToString { it.id })
    }
}