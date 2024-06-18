package com.perrigogames.life4.feature.firstrun

import com.perrigogames.life4.util.Destination

sealed class FirstRunDestination(override val baseRoute: String) : Destination {

    data object Landing : FirstRunDestination("landing")
    data object FirstRun : FirstRunDestination("first_run")
    data object PlacementList : FirstRunDestination("placement_list")
    data class PlacementDetails(val placementId: String) : FirstRunDestination(BASE_ROUTE) {
        override val route = baseRoute.replace("{$PLACEMENT_ID}", placementId)
        companion object {
            const val PLACEMENT_ID = "placement_id"
            const val BASE_ROUTE = "placement_details/{$PLACEMENT_ID}"
        }
    }
    data object InitialRankList : FirstRunDestination("initial_rank_list")
    data object MainScreen : FirstRunDestination("main_screen")
}