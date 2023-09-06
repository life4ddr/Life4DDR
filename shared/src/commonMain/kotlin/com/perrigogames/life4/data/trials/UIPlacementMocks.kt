package com.perrigogames.life4.data.trials

import com.perrigogames.life4.MR
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.LadderRank
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

object UIPlacementMocks {
    fun createUIPlacementScreen(
        headerText: StringDesc = MR.strings.placement_description_text.desc(),
        placements: List<UIPlacementData> = listOf(
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
    ) = UIPlacementScreen(
        headerText = headerText,
        placements = placements,
    )

    fun createUIPlacementData(
        rankIcon: LadderRank = LadderRank.BRONZE5,
        difficultyRangeString: String = "L7-L10",
        songs: List<UIPlacementSong> = bags,
    ) = UIPlacementData(
        rankIcon = rankIcon,
        difficultyRangeString = difficultyRangeString,
        songs = songs,
    )

    fun createUIPlacementSong(
        jacketUrl: String = "https://life4-mobile.s3.us-west-1.amazonaws.com/images/jackets/songs/bag-jacket.webp",
        songNameText: String = "bag",
        artistText: String = "REVEN-G",
        difficultyClass: DifficultyClass = DifficultyClass.EXPERT,
        difficultyText: String = "13",
        difficultyNumber: Int = 13,
    ) = UIPlacementSong(
        jacketUrl = jacketUrl,
        songNameText = songNameText,
        artistText = artistText,
        difficultyClass = difficultyClass,
        difficultyText = difficultyText,
        difficultyNumber = difficultyNumber,
    )

    val bags = listOf(
        createUIPlacementSong(
            songNameText = "bag",
            difficultyClass = DifficultyClass.DIFFICULT,
            difficultyText = "13",
            difficultyNumber = 13,
        ),
        createUIPlacementSong(
            songNameText = "bag",
            difficultyClass = DifficultyClass.EXPERT,
            difficultyText = "13",
            difficultyNumber = 13,
        ),
        createUIPlacementSong(
            songNameText = "bag",
            difficultyClass = DifficultyClass.CHALLENGE,
            difficultyText = "13",
            difficultyNumber = 13,
        )
    )
}