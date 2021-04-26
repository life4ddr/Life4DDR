package com.perrigogames.life4.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.perrigogames.life4.SettingsKeys.KEY_LIST_SHOW_EX
import com.perrigogames.life4.SettingsKeys.KEY_LIST_SHOW_EX_REMAINING
import com.perrigogames.life4.SettingsKeys.KEY_LIST_TINT_COMPLETED
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.databinding.ItemTrialListItemBinding
import com.russhwolf.settings.Settings
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * A custom [View] for displaying a single [Trial].
 */
class TrialItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), KoinComponent {

    private val settings: Settings by inject()

    private val binding = ItemTrialListItemBinding.bind(this)

    private val tintCompleted: Boolean
        get() = settings.getBoolean(KEY_LIST_TINT_COMPLETED, false)
    private val showEx: Boolean
        get() = trial?.isEvent == true || settings.getBoolean(KEY_LIST_SHOW_EX, false)
    private var usesBadgeList = false

    fun setCornerType(v: JacketCornerView.CornerType?) {
        binding.imageRank.root.setCornerType(v)
    }

    var trial: Trial? = null
        set(v) {
            field = v
            binding.imageRank.root.trial = v
            update()
        }

    private fun update() {
        if (usesBadgeList) {
            binding.imageBadge1.visibility = GONE
            binding.imageBadge2.visibility = GONE
            binding.imageBadge3.visibility = GONE
            binding.imageBadge4.visibility = GONE
            binding.imageBadge5.visibility = GONE
            binding.imageBadge6.visibility = GONE
        }
    }

    fun setHighestRank(rank: TrialRank?) {
        binding.imageRank.root.let { view ->
            view.rank = rank
            view.tintOnRank = if (trial?.isEvent != true && tintCompleted) TrialRank.values().last() else null
        }
    }

    fun setExScore(exScore: Int?) {
        binding.imageRank.root.let { view ->
            view.showExRemaining = trial?.isEvent == true ||
                    settings.getBoolean(KEY_LIST_SHOW_EX_REMAINING, false)
            view.exScore = if (showEx) exScore else null
        }
    }

    class TrialViewHolder(val trialItemView: TrialItemView): RecyclerView.ViewHolder(trialItemView)

    companion object {

        fun inflateListItem(context: Context, parent: ViewGroup, attachToRoot: Boolean) =
            LayoutInflater.from(context).inflate(R.layout.item_trial_list_item, parent, attachToRoot) as TrialItemView

        fun inflateTileItem(context: Context, parent: ViewGroup, attachToRoot: Boolean) =
            LayoutInflater.from(context).inflate(R.layout.item_trial_tile_item, parent, attachToRoot) as TrialItemView
    }
}
