package com.perrigogames.life4.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

/**
 * An [ActivityResultContract] that asks for a list of scores from another source, usually DDR A Score manager
 */
class GetScoreList : ActivityResultContract<Unit, List<String>>() {
    override fun createIntent(
        context: Context,
        input: Unit,
    ) = Intent(ACTION_RETRIEVE_SCORE)

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): List<String> {
        resultCode == Activity.RESULT_OK || return emptyList()
        val count = intent?.getIntExtra("SET_COUNT", 0) ?: 0
        return (0..count).flatMap {
            intent?.getStringExtra("SCORE_DATA_$it")?.split("\n")
                ?: emptyList()
        }.filterNot { it.isEmpty() }
    }

    companion object {
        const val ACTION_RETRIEVE_SCORE = "jp.linanfine.dsma.GET_SCORE_DATA"
    }
}
