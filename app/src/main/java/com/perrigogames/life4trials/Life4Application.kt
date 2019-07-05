package com.perrigogames.life4trials

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.perrigogames.life4trials.api.FirebaseUtil
import com.perrigogames.life4trials.db.MyObjectBox
import com.perrigogames.life4trials.manager.*
import com.perrigogames.life4trials.util.NotificationUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil
import com.perrigogames.life4trials.util.SharedPrefsUtil.KEY_APP_CRASHED
import io.objectbox.BoxStore
import org.greenrobot.eventbus.EventBus

class Life4Application: MultiDexApplication() {

    lateinit var ladderManager: LadderManager
    lateinit var placementManager: PlacementManager
    lateinit var songDataManager: SongDataManager
    lateinit var tournamentManager: TournamentManager
    lateinit var trialManager: TrialManager

    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            SharedPrefsUtil.setUserFlag(this@Life4Application, KEY_APP_CRASHED, true)
            defaultHandler!!.uncaughtException(thread, exception)
        }

        SharedPrefsUtil.initializeDefaults(this)

        objectBox = MyObjectBox.builder()
            .androidContext(this)
            .build()

        songDataManager = SongDataManager()
        placementManager = PlacementManager(this)
        trialManager = TrialManager(this)
        ladderManager = LadderManager(this, songDataManager)
        tournamentManager = TournamentManager()

        NotificationUtil.setupNotifications(this)

        if (BuildConfig.DEBUG) {
            FirebaseUtil.getId(this)
        }
    }

    companion object {
        val eventBus = EventBus()

        lateinit var objectBox: BoxStore
    }
}

val Context.life4app get() = applicationContext as Life4Application