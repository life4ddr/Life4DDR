package com.perrigogames.life4trials.manager

import android.content.Context
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.LadderRankData
import com.perrigogames.life4trials.db.GoalStatus
import com.perrigogames.life4trials.db.GoalStatusDB
import com.perrigogames.life4trials.db.GoalStatusDB_
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString
import io.objectbox.kotlin.query
import java.util.*

class LadderManager(context: Context) {

    val ladderData = DataUtil.gson.fromJson(context.loadRawString(R.raw.ranks), LadderRankData::class.java)!!

    private val objectBox get() = Life4Application.objectBox

    private val goalsBox get() = objectBox.boxFor(GoalStatusDB::class.java)

    fun setGoalState(goal: BaseRankGoal, status: GoalStatus) {
        goalsBox.query {
            var goalDB = equal(GoalStatusDB_.goalId, goal.id.toLong())
                .build().find().firstOrNull()

            if (goalDB != null) {
                goalDB.date = Date()
                goalDB.status = status
            } else {
                goalDB = GoalStatusDB(goal.id.toLong(), Date(), status)
            }
            goalsBox.put(goalDB)
        }
    }
}