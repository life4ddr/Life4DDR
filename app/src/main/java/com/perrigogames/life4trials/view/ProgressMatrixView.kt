package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import androidx.core.util.getOrDefault
import com.perrigogames.life4trials.R
import com.perrigogames.life4trials.data.BaseRankGoal
import com.perrigogames.life4trials.data.DifficultyClearGoal
import com.perrigogames.life4.data.TrialData
import com.perrigogames.life4trials.util.CommonSizes
import com.perrigogames.life4trials.util.colorRes
import com.perrigogames.life4trials.util.visibilityBool
import kotlinx.android.synthetic.main.item_progress_matrix.view.progress_amount
import kotlinx.android.synthetic.main.item_progress_matrix.view.text_goal_progress
import kotlinx.android.synthetic.main.item_progress_matrix.view.text_goal_title
import kotlinx.android.synthetic.main.item_progress_matrix_lamp.view.*
import kotlinx.android.synthetic.main.view_matrix_table_row.view.*

class ProgressMatrixView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    TableLayout(context, attrs) {

    init {
        (1..9).forEach { setColumnStretchable(it, true) }
//        showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
//        dividerDrawable = ColorDrawable(ContextCompat.getColor(context, R.color.white_70))
    }

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
        removeAllViews()
        if (goals.isNullOrEmpty()) {
            return
        }

        val inflater = LayoutInflater.from(context)
        categorizedGoals.forEach { diff, goals ->
            addView((inflater.inflate(R.layout.view_matrix_table_row, this, false) as TableRow).also { row ->
                row.text_difficulty.text = diff.toString()

                var hasLamp = false
                goals.forEachIndexed { idx, goal ->
                    row.addView(inflater.inflate(R.layout.item_progress_matrix_lamp, row, false).also { goalCell ->
                        goalCell.layoutParams = TableRow.LayoutParams(0, 150).also {
                            it.span = 6 / goals.size
                            val margin = CommonSizes.contentPaddingMed(resources)
                            it.setMargins(margin, 0, 0, margin)
                        }
                        val folderClear = goal.count == null && !hasLamp
                        listOf(goalCell.image_banner_start, goalCell.image_banner_end).forEach { banner ->
                            banner.visibilityBool = folderClear
                            if (folderClear) {
                                banner.setColorFilter(ContextCompat.getColor(context, goal.clearType.colorRes))
                                banner.layoutParams = banner.layoutParams.also {
                                    it.width= 20
                                }
                            }
                        }
                        hasLamp = hasLamp || folderClear
                        goalCell.text_goal_title.text = goal.matrixString(context)

                        val progress = 6
                        val max = goal.count ?: 100
                        goalCell.text_goal_progress.text = "$progress / $max"
                        goalCell.progress_amount.max = max
                        goalCell.progress_amount.progress = progress
                    })
                }
            })
        }
    }
}
