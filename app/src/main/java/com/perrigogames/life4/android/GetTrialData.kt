package com.perrigogames.life4.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.db.TrialDatabaseHelper
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * An [ActivityResultContract] that asks for a list of Trials, usually from an older version of the app
 */
class GetTrialData: ActivityResultContract<Unit, List<String>>() {
    override fun createIntent(context: Context, u: Unit?) = Intent(ACTION_RETRIEVE_TRIALS)

    override fun parseResult(resultCode: Int, result: Intent?): List<String>? {
        resultCode == Activity.RESULT_OK || return null
        val count = result?.getIntExtra(EXTRA_TRIAL_COUNT, 0) ?: 0
        return (0 until count).mapNotNull { result?.getStringExtra("${EXTRA_TRIAL}_$it") }
    }

    companion object {
        const val ACTION_RETRIEVE_TRIALS = "life4.intent.GET_TRIAL_DATA"
        const val EXTRA_TRIAL_COUNT = "EXTRA_TRIAL_COUNT"
        const val EXTRA_TRIAL = "EXTRA_TRIAL"
    }
}

open class TrialDataActivity: AppCompatActivity(), KoinComponent {

    private val trialDb: TrialDatabaseHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val export = trialDb.createRecordExportStrings()
        intent.putExtra(GetTrialData.EXTRA_TRIAL_COUNT, export.size)
        export.forEachIndexed { idx, s ->
            intent.putExtra("${GetTrialData.EXTRA_TRIAL}_$idx", s)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
