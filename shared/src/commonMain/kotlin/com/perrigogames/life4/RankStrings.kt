package com.perrigogames.life4

import com.perrigogames.life4.data.DifficultyClassSet
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.nameRes
import com.perrigogames.life4.feature.trials.enums.TrialRank
import dev.icerock.moko.resources.desc.Composition
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

object RankStrings {
    /** Burn X calories in one day. */
    fun getCalorieCountString(count: Int) = StringDesc.ResourceFormatted(MR.strings.rank_goal_calories, count)

    /**
     * Complete X different DIFF's in a row.
     * Complete a set of DIFF, DIFF, and DIFF.
     */
    fun getSongSetString(clearType: ClearType, difficulties: IntArray): StringDesc {
        return if (difficulties.all { it == difficulties[0] }) {
            StringDesc.ResourceFormatted(
                MR.strings.rank_goal_set_sequential,
                clearString(clearType, false),
                StringDesc.ResourceFormatted(
                    MR.strings.set_numbers_multiple_same_format,
                    difficulties.size,
                    difficulties[0]
                )
            )
        } else {
            StringDesc.ResourceFormatted(
                MR.strings.rank_goal_set_different,
                clearString(clearType, false),
                StringDesc.ResourceFormatted(
                    MR.strings.set_numbers_3_format,
                    difficulties[0],
                    difficulties[1],
                    difficulties[2]
                )
            )
        }
    }

    /**
     * Earn RANK on any Trial.
     * Earn RANK on X Trials.
     */
    fun getTrialCountString(rank: TrialRank, count: Int): StringDesc {
        return if (count == 1) StringDesc.ResourceFormatted(MR.strings.rank_goal_clear_trial_single, StringDesc.ResourceFormatted(rank.nameRes))
        else StringDesc.ResourceFormatted(MR.strings.rank_goal_clear_trial, StringDesc.ResourceFormatted(rank.nameRes), count)
    }

    /** Earn X MA Points. */
    fun getMAPointString(count: Double): StringDesc = StringDesc.ResourceFormatted(MR.strings.rank_goal_ma_points, count)

    // Song Set
    // any Mix or Letter folder / any 3 Mix or Letter folders
    fun folderString(
        folderCount: Int,
    ): StringDesc = when (folderCount) {
        1 -> StringDesc.ResourceFormatted(
            MR.strings.any_folder,
        )
        else -> StringDesc.ResourceFormatted(
            MR.strings.any_folder_plural,
            folderCount,
        )
    }

    // the 1st Mix folder
    fun folderString(
        folderName: String,
    ): StringDesc = StringDesc.ResourceFormatted(
        MR.strings.specific_folder,
        folderName,
    )

    // New Century, Rising Fire Hawk, and Astrogazer
    fun songListString(songs: List<String>): StringDesc =
        songs.toStringDescs().toListString(useAnd = true, caps = false)

    // any 3 songs
    fun songCountString(songCount: Int): StringDesc = StringDesc.ResourceFormatted(
        MR.strings.song_count,
        songCount,
    )

    // Score 945,000 on <group> / AAA <group>
    fun scoreString(score: Int, groupString: StringDesc): StringDesc = when (score) {
        GameConstants.MAX_SCORE -> throw IllegalArgumentException("Use MFC clear type")
        GameConstants.AAA_SCORE -> StringDesc.ResourceFormatted(
            MR.strings.rank_goal_diff_score_aaa,
            groupString,
        )
        else -> StringDesc.ResourceFormatted(
            MR.strings.rank_goal_diff_score,
            score.longNumberString(),
            groupString,
        )
    }

    // Score an average of 999,500 on <group>
    fun averageScoreString(averageScore: Int, groupString: StringDesc): StringDesc =
        StringDesc.ResourceFormatted(
            MR.strings.rank_goal_diff_average,
            averageScore.longNumberString(),
            groupString,
        )

    // Clear <group>
    fun clearString(clearType: ClearType, useLamp: Boolean, groupString: StringDesc): StringDesc =
        StringDesc.ResourceFormatted(
            MR.strings.rank_goal_diff_clear_single,
            clearString(clearType, useLamp),
            groupString,
        )

    fun clearString(clearType: ClearType, useLamp: Boolean) =
        if (useLamp) clearLampString(clearType)
        else clearTypeString(clearType)

    fun clearTypeString(clearType: ClearType): StringDesc = StringDesc.ResourceFormatted(when(clearType) {
        ClearType.CLEAR -> MR.strings.clear
        ClearType.LIFE4_CLEAR -> MR.strings.clear_life4
        ClearType.GOOD_FULL_COMBO -> MR.strings.clear_fc
        ClearType.GREAT_FULL_COMBO -> MR.strings.clear_gfc
        ClearType.PERFECT_FULL_COMBO -> MR.strings.clear_pfc
        ClearType.SINGLE_DIGIT_PERFECTS -> MR.strings.clear_sdp
        ClearType.MARVELOUS_FULL_COMBO -> MR.strings.clear_mfc
        else -> throw IllegalArgumentException("Illegal clear lamp $clearType")
    })

    fun clearLampString(clearType: ClearType): StringDesc = StringDesc.ResourceFormatted(when(clearType) {
        ClearType.CLEAR -> MR.strings.lamp_clear
        ClearType.LIFE4_CLEAR -> MR.strings.lamp_life4
        ClearType.GOOD_FULL_COMBO -> MR.strings.lamp_fc
        ClearType.GREAT_FULL_COMBO -> MR.strings.lamp_gfc
        ClearType.PERFECT_FULL_COMBO -> MR.strings.lamp_pfc
        ClearType.MARVELOUS_FULL_COMBO -> MR.strings.lamp_mfc
        else -> throw IllegalArgumentException("Illegal clear lamp $clearType")
    })

    // on ESP and CSP / on DSP, ESP, or CSP
    fun difficultyClassSetModifier(
        groupString: StringDesc,
        diffClassSet: DifficultyClassSet,
        playStyle: PlayStyle
    ): StringDesc = StringDesc.ResourceFormatted(
        MR.strings.difficulty_modifier,
        groupString,
        diffClassSet.set
            .map { StringDesc.Raw(it.aggregateString(playStyle)) }
            .toListString(diffClassSet.requireAll, false),
    )

    // (except 5)
    fun exceptionsModifier(groupString: StringDesc, exceptions: Int): StringDesc = StringDesc.ResourceFormatted(
        MR.strings.exceptions_num_modifier,
        groupString,
        exceptions,
    )

    // (except 5, which require 999,500)
    fun steppedExceptionsModifier(
        groupString: StringDesc,
        exceptions: Int,
        exceptionScore: Int
    ): StringDesc = StringDesc.ResourceFormatted(
        MR.strings.exceptions_num_score_modifier,
        groupString,
        exceptions,
        exceptionScore.longNumberString(),
    )

    // (except SongA, SongB, and SongC)
    fun songExceptionsModifier(groupString: StringDesc, songExceptions: List<StringDesc>): StringDesc = StringDesc.ResourceFormatted(
        MR.strings.exceptions_songs_modifier,
        groupString,
        songExceptions.toListString(useAnd = true, caps = false),
    )

    // a L15 / an L8
    fun diffNumSingle(diffNum: Int, allowsHigherDiffNum: Boolean): StringDesc = StringDesc.ResourceFormatted(
        MR.strings.a_item,
        "L$diffNum${higherDiffNumSuffix(allowsHigherDiffNum)}",
    )

    // 3 L5s
    fun diffNumCount(count: Int, diffNum: Int, allowsHigherDiffNum: Boolean): StringDesc = when(count) {
        1 -> diffNumSingle(diffNum, allowsHigherDiffNum)
        else -> StringDesc.ResourceFormatted(
            MR.strings.num_items,
            count,
            "L$diffNum${higherDiffNumSuffix(allowsHigherDiffNum)}",
        )
    }

    // all L14s
    fun diffNumAll(diffNum: Int, allowsHigherDiffNum: Boolean): StringDesc = StringDesc.ResourceFormatted(
        MR.strings.all_items,
        "L$diffNum${higherDiffNumSuffix(allowsHigherDiffNum)}",
    )

    fun higherDiffNumSuffix(allowsHigherDiffNum: Boolean): String = when (allowsHigherDiffNum) {
        true -> "+"
        false -> ""
    }
}
