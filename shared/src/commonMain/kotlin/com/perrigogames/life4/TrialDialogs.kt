package com.perrigogames.life4

import com.perrigogames.life4.data.TrialRank
import org.koin.core.KoinComponent

interface TrialDialogs: KoinComponent {

    fun showRankConfirmation(rank: TrialRank, result: (Boolean) -> Unit)
    fun showSessionSubmitConfirmation(result: (Boolean) -> Unit)
    fun showTrialSubmissionWeb()
}