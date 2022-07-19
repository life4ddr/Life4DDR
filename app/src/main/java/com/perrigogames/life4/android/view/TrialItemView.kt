package com.perrigogames.life4.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.perrigogames.life4.SettingsKeys.KEY_LIST_SHOW_EX
import com.perrigogames.life4.SettingsKeys.KEY_LIST_SHOW_EX_REMAINING
import com.perrigogames.life4.SettingsKeys.KEY_LIST_TINT_COMPLETED
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.MergeTrialTileItemBinding
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.enums.TrialJacketCorner
import com.perrigogames.life4.enums.TrialRank
import com.russhwolf.settings.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * A custom [View] for displaying a single [Trial].
 */
class TrialItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), KoinComponent {

    private val settings: Settings by inject()

    private val binding: MergeTrialTileItemBinding

    init {
        LayoutInflater.from(context).inflate(R.layout.merge_trial_tile_item, this)
        binding = MergeTrialTileItemBinding.bind(this)
    }

    private val tintCompleted: Boolean
        get() = settings.getBoolean(KEY_LIST_TINT_COMPLETED, false)
    private val showEx: Boolean
        get() = trial?.isEvent == true || settings.getBoolean(KEY_LIST_SHOW_EX, false)

    fun setCornerType(v: TrialJacketCorner?) {
        binding.imageRank.setCornerType(v)
    }

    var trial: Trial? = null
        set(v) {
            field = v
            binding.imageRank.trial = v
        }

    fun setHighestRank(rank: TrialRank?) {
        binding.imageRank.let { view ->
            view.rank = rank
            view.tintOnRank = if (trial?.isEvent != true && tintCompleted) TrialRank.values().last() else null
        }
    }

    fun setExScore(exScore: Int?) {
        binding.imageRank.let { view ->
            view.showExRemaining = trial?.isEvent == true ||
                    settings.getBoolean(KEY_LIST_SHOW_EX_REMAINING, false)
            view.exScore = if (showEx) exScore else null
        }
    }

    companion object {

        fun inflate(context: Context, parent: ViewGroup, attachToRoot: Boolean) =
            LayoutInflater.from(context).inflate(R.layout.item_trial_tile_item, parent, attachToRoot) as TrialItemView
    }
}
