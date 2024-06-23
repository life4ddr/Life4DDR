package com.perrigogames.life4.feature.ladder

import com.perrigogames.life4.MR
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.LadderRankClass
import com.perrigogames.life4.enums.categoryNameRes
import com.perrigogames.life4.enums.nameRes
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

data class UIRankList(
    val titleText: StringResource = MR.strings.select_a_new_rank,
    val showBackButton: Boolean = false,
    val rankClasses: List<UILadderRankClass> = emptyList(),
    val selectedRankClass: LadderRankClass? = null,
    val showRankSelector: Boolean = false,
    val ranks: List<UILadderRank> = emptyList(),
    val isRankSelectorCompressed: Boolean = false,
    val noRankInfo: UINoRank = UINoRank.DEFAULT,
    val footer: UIFooterData? = null,
    val ladderData: UILadderData? = null,
)

data class UILadderRankClass(
    val rankClass: LadderRankClass?,
    val text: StringDesc = rankClass.nameRes.desc(),
    val selected: Boolean = false,
    val tapInput: RankListViewModel.Input = RankListViewModel.Input.RankClassTapped(rankClass),
) {
    companion object {
        val NO_RANK = UILadderRankClass(
            rankClass = null,
            text = MR.strings.no_rank.desc(),
        )
    }
}

data class UILadderRank(
    val rank: LadderRank,
    val text: StringDesc = rank.categoryNameRes.desc(),
    val selected: Boolean = false,
    val tapInput: RankListViewModel.Input = RankListViewModel.Input.RankTapped(rank),
)

data class UIFooterData(
    val footerText: StringDesc? = null,
    val buttonText: StringDesc,
    val buttonInput: RankListViewModel.Input,
) {
    companion object {
        val FIRST_RUN_CANCEL = UIFooterData(
            footerText = MR.strings.change_rank_later.desc(),
            buttonText = MR.strings.play_placement_instead.desc(),
            buttonInput = RankListViewModel.Input.MoveToPlacements,
        )
        val FIRST_RUN_NO_RANK_SUBMIT = UIFooterData(
            footerText = MR.strings.change_rank_later.desc(),
            buttonText = MR.strings.start_with_no_rank.desc(),
            buttonInput = RankListViewModel.Input.RankRejected,
        )
        fun firstRunRankSubmit(rank: LadderRank) = UIFooterData(
            footerText = MR.strings.change_rank_later.desc(),
            buttonText = StringDesc.ResourceFormatted(
                MR.strings.start_with_rank_format,
                rank.nameRes.desc(),
            ),
            buttonInput = RankListViewModel.Input.RankSelected(rank),
        )
        val CHANGE_NO_RANK_SUBMIT = UIFooterData(
            buttonText = MR.strings.change_to_no_rank.desc(),
            buttonInput = RankListViewModel.Input.RankRejected,
        )
        fun changeSubmit(rank: LadderRank) = UIFooterData(
            buttonText = StringDesc.ResourceFormatted(
                MR.strings.change_to_rank_format,
                rank.nameRes.desc(),
            ),
            buttonInput = RankListViewModel.Input.RankSelected(rank),
        )
    }
}

data class UINoRank(
    val bodyText: StringResource,
    val buttonText: StringResource,
    val buttonInput: RankListViewModel.Input = RankListViewModel.Input.RankRejected,
) {
    companion object {
        val DEFAULT = UINoRank(
            bodyText = MR.strings.no_rank_goals,
            buttonText = MR.strings.i_have_no_rank,
        )
        val FIRST_RUN = UINoRank(
            bodyText = MR.strings.no_rank_goals,
            buttonText = MR.strings.start_with_no_rank,

        )
    }
}