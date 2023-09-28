package com.perrigogames.life4trials.manager

import android.content.Context
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialData
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString

class PlacementManager(context: Context) {

    private val placementData: TrialData =
        DataUtil.gson.fromJson(context.loadRawString(R.raw.placements), TrialData::class.java)!!
    val placements get() = placementData.trials
}