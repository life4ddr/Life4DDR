package com.perrigogames.life4.data.trials

import com.perrigogames.life4.MR
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.trials.view.UITrialSong
import com.perrigogames.life4.feature.trials.view.bags
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

object UIPlacementMocks {
    fun createUIPlacementScreen(
        titleText: StringDesc = MR.strings.placements.desc(),
        headerText: StringDesc = MR.strings.placement_list_description.desc(),
        placements: List<UIPlacement> = listOf(
            createUIPlacementData(),
            createUIPlacementData(
                rankIcon = LadderRank.SILVER5,
                difficultyRangeString = "L11-L13",
            ),
            createUIPlacementData(
                rankIcon = LadderRank.GOLD5,
                difficultyRangeString = "L14-L16",
            ),
        ),
    ) = UIPlacementListScreen(
        titleText = titleText,
        headerText = headerText,
        placements = placements,
    )

    fun createUIPlacementData(
        id: String = "placement_id",
        rankIcon: LadderRank = LadderRank.BRONZE5,
        difficultyRangeString: String = "L7-L10",
        songs: List<UITrialSong> = bags,
    ) = UIPlacement(
        id = id,
        rankIcon = rankIcon,
        difficultyRangeString = difficultyRangeString,
        songs = songs,
    )
}