package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.LadderGoalProgress
import com.perrigogames.life4trials.db.GoalStatus
import com.perrigogames.life4trials.db.GoalStatus.COMPLETE
import com.perrigogames.life4trials.db.GoalStatus.IGNORED
import com.perrigogames.life4trials.db.GoalStatusDB
import com.perrigogames.life4trials.util.visibilityBool
import kotlinx.android.synthetic.main.item_rank_goal.view.*

class LadderGoalItemView @JvmOverloads constructor(context: Context,
                                                   attrs: AttributeSet? = null,
                                                   defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var goal: BaseRankGoal? = null
    private var goalDB: GoalStatusDB? = null
        get() {
            if (field == null) {
                field = goal?.let { listener?.createGoalDB(it) }
            }
            return field
        }
    private var goalProgress: LadderGoalProgress? = null

    var listener: LadderGoalItemListener? = null
    var expanded: Boolean = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        button_status_icon.setOnClickListener { goal?.let { g ->
            goalDB?.let { db ->
                listener?.onStateToggle(this, g, db)
                updateData()
            }
        } }
        button_ignore.setOnClickListener { goal?.let { g ->
            goalDB?.let { db ->
                listener?.onIgnoreClicked(this, g, db)
                updateData()
            }
        } }
        setOnClickListener { goal?.let { g ->
            goalDB?.let { db ->
                if (text_expand_left.text.isNotEmpty()) {
                    listener?.onExpandClicked(this, g, db)
                }
            }
        } }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        button_status_icon.setOnClickListener(null)
        button_ignore.setOnClickListener(null)
    }

    fun setGoal(goal: BaseRankGoal, goalDB: GoalStatusDB? = null, goalProgress: LadderGoalProgress? = null) {
        this.goal = goal
        this.goalDB = goalDB
        this.goalProgress = goalProgress
        updateData()
    }

    fun updateData() {
        val state = goalDB?.status ?: GoalStatus.INCOMPLETE
        text_goal_title.text = goal?.goalString(context) ?: ""

        goalProgress?.let {
            text_goal_subtitle.text = context.getString(R.string.goal_progress_format, it.progress, it.max)
            it.results?.let { results ->
                text_expand_left.text = results.joinToString("\n") { r -> r.score.longNumberString() }
                text_expand_right.text = results.joinToString("\n") { r -> r.chart.target.song.target.title }
            }
        }

        text_expand_left.visibilityBool = expanded
        text_expand_right.visibilityBool = expanded

        text_goal_subtitle.visibilityBool = goalProgress != null
        button_status_icon.isChecked = state == COMPLETE
        button_ignore.visibilityBool = goal?.mandatory != true
        alpha = if (state == IGNORED) 0.3f else 1.0f
    }

    interface LadderGoalItemListener {

        fun createGoalDB(item: BaseRankGoal): GoalStatusDB
        fun onStateToggle(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
        fun onIgnoreClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
        fun onExpandClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
    }
}