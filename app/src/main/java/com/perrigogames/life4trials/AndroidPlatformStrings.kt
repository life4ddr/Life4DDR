package com.perrigogames.life4trials

import android.content.Context
import android.net.Uri
import com.perrigogames.life4.PlatformStrings
import com.perrigogames.life4.data.*
import com.perrigogames.life4.enums.ClearType
import org.koin.core.KoinComponent
import org.koin.core.inject

class AndroidPlatformStrings: PlatformStrings, KoinComponent {

    private val c: Context by inject()

    override val rank = AndroidRankStrings(c)
    override val trial = AndroidTrialStrings(c)

    override fun nameString(rank: LadderRank) = c.getString(rank.nameRes)
    override fun groupNameString(rank: LadderRank) = c.getString(rank.groupNameRes)
    override fun nameString(rank: TrialRank) = c.getString(rank.nameRes)
    override fun nameString(rank: PlacementRank) = c.getString(rank.nameRes)
    override fun nameString(clazz: LadderRankClass) = c.getString(clazz.nameRes)
    override fun lampString(ct: ClearType) = c.getString(ct.lampRes)
    override fun clearString(ct: ClearType) = c.getString(ct.clearRes)
    override fun clearStringShort(ct: ClearType) = c.getString(ct.clearResShort)

    override fun toListString(list: List<String>, useAnd: Boolean, caps: Boolean): String = list.toListString(c, useAnd, caps)
}

fun List<String>.toListString(c: Context, useAnd: Boolean, caps: Boolean): String = StringBuilder().apply {
    this@toListString.forEachIndexed { index, d ->
        append(when {
            this@toListString.size == 1 -> d
            index == this@toListString.lastIndex -> c.getString(when {
                useAnd -> if (caps) R.string.and_s_caps else R.string.and_s
                else -> if (caps) R.string.or_s_caps else R.string.or_s
            }, d)
            index == this@toListString.lastIndex - 1 -> "$d "
            else -> "$d, "
        })
    }
}.toString()

var TrialSession.finalPhotoUri: Uri
    get() = Uri.parse(finalPhotoUriString)
    set(value) { finalPhotoUriString = value.toString() }

var SongResult.photoUri: Uri
    get() = Uri.parse(photoUriString)
    set(value) { photoUriString = value.toString() }
