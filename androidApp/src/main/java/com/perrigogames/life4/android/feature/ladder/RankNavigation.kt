package com.perrigogames.life4.android.feature.ladder

import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.ladder.LadderDestination

fun NavGraphBuilder.ladderNavigation(navController: NavController) {

    composable(LadderDestination.RankList.baseRoute) {
        Text("Rank List TODO")
    }

    composable(
        route = LadderDestination.RankDetails.BASE_ROUTE,
        arguments = listOf(
            navArgument(LadderDestination.RankDetails.RANK_ID) { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val rankId = backStackEntry.arguments?.getLong(LadderDestination.RankDetails.RANK_ID)
        LadderGoalsScreen(
            targetRank = LadderRank.parse(rankId)
        )
    }
}