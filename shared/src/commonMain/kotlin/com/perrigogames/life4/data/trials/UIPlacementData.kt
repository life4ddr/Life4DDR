package com.perrigogames.life4.data.trials

import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.enums.groupNameRes
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc

data class UIPlacementScreen(
    val headerText: StringDesc,
    val placements: List<UIPlacementData>,
)

data class UIPlacementData(
    val rankIcon: LadderRank,
    val difficultyRangeString: String = "", // FIXME resource
    val songs: List<UIPlacementSong>
) {
    val color: ColorResource = rankIcon.group.colorRes
    val placementName: StringResource = rankIcon.groupNameRes
}

data class UIPlacementSong(
    val jacketUrl: String,
    val songNameText: String,
    val artistText: String,
    val difficultyClass: DifficultyClass,
    val difficultyText: String,
    val difficultyNumber: Int,
) {
    val color: ColorResource = difficultyClass.colorRes
}