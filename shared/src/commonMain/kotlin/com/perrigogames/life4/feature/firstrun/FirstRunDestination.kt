package com.perrigogames.life4.feature.firstrun

import com.perrigogames.life4.util.Destination

sealed class FirstRunDestination(override val baseRoute: String) : Destination {

    data object Landing : FirstRunDestination("landing")
    data object FirstRun : FirstRunDestination("first_run")
    data object PlacementList : FirstRunDestination("placement_list")
    data class PlacementDetails(val placementId: String) : FirstRunDestination(BASE_ROUTE) {
        override val route = baseRoute.replace("{placement_id}", placementId)
        companion object {
            const val BASE_ROUTE = "placement_details/{placement_id}"
        }
    }
    data object InitialRankList : FirstRunDestination("initial_rank_list")
    data class RankDetails(val ladderRankId: String) : FirstRunDestination(BASE_ROUTE) {
        override val route = baseRoute.replace("{ladder_rank_id}", ladderRankId)
        companion object {
            const val BASE_ROUTE = "rank_details/{ladder_rank_id}"
        }
    }
    data object MainScreen : FirstRunDestination("main_screen")
}