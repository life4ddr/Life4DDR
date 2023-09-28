package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.perrigogames.life4trials.data.BaseRankGoal
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

    init {
        image_status_icon.setOnClickListener { goal?.let { g ->
            goalDB?.let { db ->
                listener?.onStateToggle(this, g, db)
                updateData()
            }
        } }
        image_ignore.setOnClickListener { goal?.let { g ->
            goalDB?.let { db ->
                listener?.onIgnoreClicked(this, g, db)
                updateData()
            }
        } }
    }

    fun setGoal(goal: BaseRankGoal, goalDB: GoalStatusDB? = null) {
        this.goal = goal
        this.goalDB = goalDB
        updateData()
    }

    fun updateData() {
        text_rank_title.text = goal?.goalString(context) ?: ""
    }

    interface LadderGoalItemListener {

        fun createGoalDB(item: BaseRankGoal): GoalStatusDB
        fun onStateToggle(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
        fun onIgnoreClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
    }
}