package com.perrigogames.life4.feature.trials

import com.perrigogames.life4.data.Trial

/**
 * Sealed class representing different destinations within the Trials feature.
 */
sealed class TrialDestination {

    /**
     * Destination that shows the details for a specific Trial, as well as let
     * you play it.
     * @property trial The Trial to show details for
     */
    data class TrialDetails(val trial: Trial) : TrialDestination() {
        val route = TEMPLATE.replace("{trialId}", trial.id)

        companion object {
            const val TEMPLATE = "trial/details/{trialId}"
        }
    }

    /**
     * Destination that shows a list of all the Trials this user has played.
     */
    data object TrialRecords : TrialDestination() {
        val route = "trial/records"
    }
}