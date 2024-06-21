package com.perrigogames.life4.feature.trials

import com.perrigogames.life4.MR
import com.perrigogames.life4.enums.LadderRank
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

/**
 * A View state describing the Trial list and its contents
 */
data class UITrialList(
    val placementBanner: UIPlacementBanner? = null,
    val trials: List<Item> = emptyList()
) {
    sealed class Item {
        data class Trial(val data: UITrialJacket) : Item()
        data class Header(val text: StringDesc) : Item()
    }
}

data class UIPlacementBanner(
    val text: StringDesc = MR.strings.play_placement.desc(),
    val ranks: List<LadderRank> = listOf(
        LadderRank.COPPER5,
        LadderRank.BRONZE5,
        LadderRank.SILVER5,
        LadderRank.GOLD5,
    )
)