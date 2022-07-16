package com.perrigogames.life4

import com.perrigogames.life4.data.DifficultyClassSet
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.TrialRank
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
interface RankStrings {

    /** Burn X calories in one day. */
    fun getCalorieCountString(count: Int): String

    /**
     * Complete X different DIFF's in a row.
     * Complete a set of DIFF, DIFF, and DIFF.
     */
    fun getSongSetString(clearType: ClearType = ClearType.CLEAR, difficulties: IntArray): String

    /**
     * Earn RANK on any Trial.
     * Earn RANK on X Trials.
     */
    fun getTrialCountString(rank: TrialRank, count: Int): String

    /** Earn X MFC Points. */
    fun getMFCPointString(count: Double): String

    // Song Set
    fun folderString(folderCount: Int): String  // any Mix or Letter folder / any 3 Mix or Letter folders
    fun folderString(folderName: String): String  // the 1st Mix folder
    fun songListString(songs: List<String>): String  // New Century, Rising Fire Hawk, and Astrogazer
    fun songCountString(songCount: Int): String  // any 3 songs

    fun scoreString(score: Int, groupString: String): String  // Score 945,000 on <group> / AAA <group>
    fun averageScoreString(averageScore: Int, groupString: String): String  // Score an average of 999,500 on <group>
    fun clearString(clearType: ClearType, useLamp: Boolean, groupString: String): String  // Clear <group>

    fun clearString(clearType: ClearType, useLamp: Boolean) =
        if (useLamp) clearLampString(clearType)
        else clearTypeString(clearType)
    fun clearTypeString(clearType: ClearType): String
    fun clearLampString(clearType: ClearType): String

    fun difficultyClassSetModifier(
        groupString: String,
        diffClassSet: DifficultyClassSet,
        playStyle: PlayStyle
    ): String  // on ESP and CSP / on DSP, ESP, or CSP
    fun exceptionsModifier(groupString: String, exceptions: Int): String  // (5E)
    fun songExceptionsModifier(groupString: String, songExceptions: List<String>): String  // (except SongA, SongB, and SongC)

    fun diffNumSingle(diffNum: Int, allowsHigherDiffNum: Boolean): String  // a L15 / an L8
    fun diffNumCount(count: Int, diffNum: Int, allowsHigherDiffNum: Boolean): String  // 3 L5s
    fun diffNumAll(diffNum: Int, allowsHigherDiffNum: Boolean): String  // all L14s
    fun higherDiffNumSuffix(allowsHigherDiffNum: Boolean): String = when (allowsHigherDiffNum) {
        true -> "+"
        false -> ""
    }
}
