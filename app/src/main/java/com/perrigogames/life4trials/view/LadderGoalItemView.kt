package com.perrigogames.life4trials.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.LadderGoalProgress
import com.perrigogames.life4trials.db.GoalStatus
import com.perrigogames.life4trials.db.GoalStatus.COMPLETE
import com.perrigogames.life4trials.db.GoalStatus.IGNORED
import com.perrigogames.life4trials.db.GoalStatusDB
import com.perrigogames.life4trials.util.visibilityBool
import kotlinx.android.synthetic.main.item_rank_goal.view.*
import kotlinx.android.synthetic.main.row_song_detail.view.*

class LadderGoalItemView @JvmOverloads constructor(context: Context,
                                                   attrs: AttributeSet? = null,
                                                   defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var goal: BaseRankGoal? = null
    private var goalDB: GoalStatusDB? = null
    private var goalProgress: LadderGoalProgress? = null
    private var oldColors: ColorStateList? = null

    var listener: LadderGoalItemListener? = null
    var expanded: Boolean = false

    private val currentState get() = goalDB?.status ?: GoalStatus.INCOMPLETE

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        button_status_icon.setOnClickListener { ifHasDB { g, db ->
            listener?.onStateToggle(this, g, db)
            updateData()
        } }
        button_ignore.setOnClickListener { ifHasDB { g, db ->
            listener?.onIgnoreClicked(this, g, db)
            updateData()
        } }
        setOnClickListener { ifHasDB { g, db ->
            if (table_expand_details.childCount > 0) {
                listener?.onExpandClicked(this, g, db)
            }
        } }
        setOnLongClickListener { ifHasDB { g, db ->
            listener?.onLongPressed(this, g, db)
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

    var canIgnore: Boolean = true
        set(v) {
            field = v
            updateIgnoreState()
        }

    private fun updateData() {
        if (oldColors == null) {
            oldColors = text_goal_subtitle.textColors
        }

        text_goal_title.text = goal?.goalString(context) ?: ""
        button_status_icon.isChecked = currentState == COMPLETE

        updateExpand()
        updateProgress()
        updateIgnoreState()
    }

    private fun updateExpand() {
        table_expand_details.children.forEach { pool.add(it as TableRow) }
        table_expand_details.removeAllViews()
        table_expand_details.visibilityBool = expanded
    }

    private fun updateProgress() {
        goalProgress?.let {
            text_goal_subtitle.text = if (!it.showMax) {
                it.progress.longNumberString()
            } else {
                context.getString(R.string.goal_progress_format, it.progress, it.max)
            }
            if (it.progress >= it.max) {
                text_goal_subtitle.setTextColor(ContextCompat.getColor(context, R.color.gold))
            } else {
                text_goal_subtitle.setTextColor(oldColors)
            }
            it.results?.forEach { result ->
                val name = result.chart.target.song.target.title
                val difficulty = result.chart.target.difficultyClass

                val row = retrieveTableRow(table_expand_details, result.score.longNumberString(), name)
                row.text_title.setTextColor(ContextCompat.getColor(context, difficulty.colorRes))
                table_expand_details.addView(row)
            }
        }
        text_goal_subtitle.visibilityBool = goalProgress != null
    }

    private fun updateIgnoreState() {
        button_ignore.visibility = when {
            goal?.mandatory == true -> View.GONE // mandatory goals never show ignore, reclaim space
            canIgnore || currentState == IGNORED -> View.VISIBLE // show ignore if you're allowed to, or if you're already ignored
            else -> View.INVISIBLE // ignore function is hidden, don't reclaim space
        }
        alpha = if (currentState == IGNORED) 0.3f else 1.0f
    }

    private inline fun ifHasDB(block: (BaseRankGoal, GoalStatusDB) -> Unit): Boolean {
        goal?.let { g ->
            goalDB?.let { db -> block(g, db) }
        }
        return true
    }

    interface LadderGoalItemListener {

        fun onStateToggle(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
        fun onIgnoreClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
        fun onExpandClicked(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
        fun onLongPressed(itemView: LadderGoalItemView, item: BaseRankGoal, goalDB: GoalStatusDB)
    }

    companion object {

        private val pool = mutableListOf<TableRow>()
        private fun retrieveTableRow(parent: TableLayout, scoreString: String, titleString: String): TableRow {
            val item = if (pool.isNotEmpty())
                pool.removeAt(0)
            else LayoutInflater.from(parent.context).inflate(R.layout.row_song_detail, parent, false) as TableRow

            return item.apply {
                text_score.text = scoreString
                text_title.text = titleString
            }
        }
    }
}