package com.perrigogames.life4trials.manager

import android.content.Context
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.LadderRankData
import com.perrigogames.life4trials.db.GoalStatus
import com.perrigogames.life4trials.db.GoalStatusDB
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString
import java.util.*

class LadderManager(context: Context) {

    val ladderData = DataUtil.gson.fromJson(context.loadRawString(R.raw.ranks), LadderRankData::class.java)!!

    private val objectBox get() = Life4Application.objectBox

    private val goalsBox get() = objectBox.boxFor(GoalStatusDB::class.java)

    fun getGoalStatus(goal: BaseRankGoal): GoalStatusDB? {
        return goalsBox.get(goal.id.toLong())
    }

    fun getOrCreateGoalStatus(goal: BaseRankGoal): GoalStatusDB {
        var goalDB = getGoalStatus(goal)
        if (goalDB == null) {
            goalDB = GoalStatusDB(goal.id.toLong())
            goalsBox.put(goalDB)
        }
        return goalDB
    }

    fun setGoalState(goal: BaseRankGoal, status: GoalStatus) {
        val statusDB = getGoalStatus(goal)
        if (statusDB == null) {
            goalsBox.put(GoalStatusDB(goal.id.toLong(), status))
        } else {
            setGoalState(statusDB, status)
        }
    }

    fun setGoalState(goalDB: GoalStatusDB, status: GoalStatus) {
        goalDB.date = Date()
        goalDB.status = status
        goalsBox.put(goalDB)
    }
}