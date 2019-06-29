package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.util.forEach
import androidx.core.util.getOrDefault
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.DifficultyClearGoal
import com.perrigogames.life4trials.data.TrialData
import kotlinx.android.synthetic.main.item_progress_matrix.view.*

class ProgressMatrixView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    TableLayout(context, attrs) {

    var goals: List<BaseRankGoal>? = null
        set(v) {
            field = v
            updateCategorizedGoals()
            updateContents()
        }

    private val categorizedGoals: SparseArray<MutableList<DifficultyClearGoal>> = SparseArray(TrialData.HIGHEST_DIFFICULTY)

    private fun updateCategorizedGoals() {
        categorizedGoals.clear()
        goals?.mapNotNull { it as? DifficultyClearGoal }?.filter { it.difficulty != null }?.forEach {
            val list = categorizedGoals.getOrDefault(it.difficultyNumbers[0], mutableListOf())
            list.add(it)
            categorizedGoals.put(it.difficultyNumbers[0], list)
        }
    }

    private fun updateContents() {
        if (goals.isNullOrEmpty()) {
            removeAllViews()
            return
        }

        categorizedGoals.forEach { diff, goals ->
            addView(TableRow(context).also { row ->
                row.layoutParams = TableRow.LayoutParams(MATCH_PARENT, 100)
                goals.forEachIndexed { idx, goal ->
                    val goalCell = LayoutInflater.from(context).inflate(R.layout.item_progress_matrix, row, false)
                    goalCell.layoutParams = TableRow.LayoutParams(500, 300)
                    goalCell.text_goal_title.text = goal.clearType.name
                    goalCell.text_goal_progress.text = "progress"
                    goalCell.progress_amount.max = 10
                    goalCell.progress_amount.progress = 6
//                    val goalCell = TextView(context)
//                    goalCell.text = goal.clearType.name + goal.score?.toString()
                    row.addView(goalCell, idx)
                }
            })
        }
    }
}