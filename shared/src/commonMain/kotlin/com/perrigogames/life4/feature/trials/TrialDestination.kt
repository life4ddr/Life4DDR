package com.perrigogames.life4.feature.trials

import com.perrigogames.life4.feature.trials.data.Trial
import com.perrigogames.life4.util.Destination

/**
 * Sealed class representing different destinations within the Trials feature.
 */
sealed class TrialDestination(override val baseRoute: String) : Destination {

    /**
     * Destination that shows the details for a specific Trial, as well as let
     * you play it.
     * @property trial The Trial to show details for
     */
    data class TrialDetails(val trial: Trial) : TrialDestination(BASE_ROUTE) {
        override val route = baseRoute.replace("{trialId}", trial.id)
        companion object {
            const val BASE_ROUTE = "trial/details/{trialId}"
        }
    }

    /**
     * Destination that shows a list of all the Trials this user has played.
     */
    data object TrialRecords : TrialDestination("trial/records")
}