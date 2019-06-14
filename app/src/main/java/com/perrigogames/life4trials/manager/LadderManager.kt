package com.perrigogames.life4trials.manager

import android.content.Context
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.LadderRankData
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString

class LadderManager(context: Context) {

    val ladderData = DataUtil.gson.fromJson(context.loadRawString(R.raw.ranks), LadderRankData::class.java)!!
}