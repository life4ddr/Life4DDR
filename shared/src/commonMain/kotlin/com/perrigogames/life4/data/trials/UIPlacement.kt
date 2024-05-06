package com.perrigogames.life4.data.trials

import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.enums.groupNameRes
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc

data class UIPlacementListScreen(
    val titleText: StringDesc,
    val headerText: StringDesc,
    val placements: List<UIPlacement>,
)

data class UIPlacement(
    val id: String,
    val rankIcon: LadderRank,
    val difficultyRangeString: String = "", // FIXME resource
    val songs: List<UITrialSong>,
) {
    val color: ColorResource = rankIcon.group.colorRes
    val placementName: StringResource = rankIcon.groupNameRes
}
