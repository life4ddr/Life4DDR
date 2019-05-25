package com.perrigogames.life4trials.manager

import android.content.Context
import com.perrigogames.life4trials.BuildConfig
import com.perrigogames.life4trials.Life4Application
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.TrialData
import com.perrigogames.life4trials.data.TrialSession
import com.perrigogames.life4trials.db.SongDB
import com.perrigogames.life4trials.db.TrialSessionDB
import com.perrigogames.life4trials.util.DataUtil
import com.perrigogames.life4trials.util.loadRawString

class TrialManager(context: Context) {

    private var trialData: TrialData
    val trials get() = trialData.trials

    init {
        trialData = DataUtil.gson.fromJson(context.loadRawString(R.raw.trials), TrialData::class.java)!!
        if (BuildConfig.DEBUG) {
            val debugData: TrialData = DataUtil.gson.fromJson(context.loadRawString(R.raw.trials_debug), TrialData::class.java)!!
            trialData = TrialData(trialData.trials + debugData.trials)
        }
    }

    private val objectBox get() = Life4Application.objectBox

    private val sessionBox get() = objectBox.boxFor(TrialSessionDB::class.java)
    private val songBox get() = objectBox.boxFor(SongDB::class.java)

    val records: List<TrialSessionDB> get() = sessionBox.all

    fun findTrial(id: String) = trials.firstOrNull { it.id == id }

    fun saveRecord(session: TrialSession) {
        val sessionDB = TrialSessionDB.from(session)
        songBox.put(session.results.filterNotNull().map { result ->
            SongDB.from(result).also {
                it.session.target = sessionDB
            }
        })
        sessionBox.put(sessionDB)
    }
}