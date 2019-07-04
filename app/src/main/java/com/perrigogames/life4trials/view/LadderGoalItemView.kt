package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.db.GoalStatus
import com.perrigogames.life4trials.db.GoalStatus.COMPLETE
import com.perrigogames.life4trials.db.GoalStatus.IGNORED
import com.perrigogames.life4trials.db.GoalStatusDB
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
    var listener: LadderGoalItemListener? = null

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
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        button_status_icon.setOnClickListener(null)
        button_ignore.setOnClickListener(null)
    }

    fun setGoal(goal: BaseRankGoal, goalDB: GoalStatusDB? = null) {
        this.goal = goal
        this.goalDB = goalDB
        updateData()
    }

    fun updateData() {
        val state = goalDB?.status ?: GoalStatus.INCOMPLETE
        text_rank_title.text = goal?.goalString(context) ?: ""

        button_status_icon.isChecked = state == COMPLETE
        button_ignore.visibility = if (goal?.mandatory == true) View.GONE else View.VISIBLE
        alpha = if (state == IGNORED) 0.3f else 1.0f
    }

    interface LadderGoalItemListener {

        fun createGoalDB(item: BaseRankGoal): GoalStatusDB
        fun onStateToggle(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
        fun onIgnoreClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
    }
}